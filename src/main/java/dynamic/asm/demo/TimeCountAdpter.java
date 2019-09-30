package dynamic.asm.demo;


import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.commons.AnalyzerAdapter;
import jdk.internal.org.objectweb.asm.commons.LocalVariablesSorter;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

/**
 * 在jdk 1.6以后编译的类，除了字节码指令以外，还多了一些栈映射桢（stack map frames），用来提高虚拟机校验字节码的速度的。
 * <p>
 * stack map frames反映了字节码执行过程中，栈帧的变化。
 * <p>
 * stack map frames中有一种特殊类型Uninitialized(label)，它先分配内存，但是不初始化，它只有初始化方法可以被调用。一旦被初始化，则发生在这个类型上的所有事件都会被替换为真实的类型。比如IllegalArgumentException。
 * <p>
 * stack map frames还有其它3种特殊类型：
 * UNINITIALIZED_THIS 是构造函数中局部变量表第一个元素。
 * TOP 相当于一个未定义的值
 * NULL 等价于 null
 * <p>
 * 为了节省空间，只在特殊指令后保存stack map frames，比如
 * jump跳转指令，exception处理指令，无条件跳转指令。
 * <p>
 * 为了节省更多空间，只保存每一帧与上一帧不同的地方。初始桢不保存，因为这可以很容易从方法参数类型中推导出来。
 * <p>
 * ASM使用MethodVisitor产生和修改方法，MethodVisitor类的方法调用有顺序要求：
 * <p>
 * <p>
 * ASM提供了三种基于MethodVisitor的核心组件，用来产生和修改方法：
 * <p>
 * 通过ClassReader解析字节码，然后调用classVisitor返回的methodVisitor中相应的方法。这个classVitor是ClassReader.accept()的参数。
 * ClassWriter 的visitMethod方法返回了一个methodVisitor的实现，可以直接产生二进制的字节码。
 * methodVisitor委托调用其它methodVisitor示例，这种可以看作过滤器
 * ClassWriter的参数：
 * <p>
 * 0，你需要手动计算，最大操作数栈，局部变量表，桢变化
 * ClassWriter.COMPUTE_MAXS，自动计算局部变量表和操作数栈，但是必须要调用visitMaxs，方法参数会被忽略。桢变化需要手动计算
 * ClassWriter.COMPUTE_FRAMES，全自动计算，但是必须要调用visitMaxs，方法参数会被忽略。
 * 但是有时间成本，ClassWriter.COMPUTE_MAXS比0慢10%，COMPUTE_FRAMES慢一倍。
 * 在特定情况下的特定算法可能比ASM提供的更快，因为ASM需要考虑所有情况。
 * <p>
 * 下面是给目标类的所有方法添加计时的代码，使用一个局部变量计时，然后打印时间，纳秒级别的。
 * <p>
 * 使用AnalyzerAdapter计算最大操作数栈，LocalVariablesSorter重新计算局部变量的索引并自动更新字节码中的索引引用。
 * <p>
 * 使用MethodVisitor修改字节码，还可以限定类名，只修改指定类名的类方法。
 * <p>
 * 因为插入了新的局部变量用于计时，所以需要重新定位局部变量。
 * <p>
 * 效果：
 * <p>
 * 原始类：
 * <p>
 * public class Receiver {
 * public void do1(){
 * System.out.println("开工12");
 * }
 * }
 * 修改之后的类：
 * <p>
 * public void do1() {
 * long var1 = System.nanoTime();
 * System.out.println("开工12");
 * var1 = System.nanoTime() - var1;
 * System.out.println(var1);
 * }
 */
public class TimeCountAdpter extends ClassVisitor implements Opcodes {
    private String owner;
    private boolean isInterface;

    public TimeCountAdpter(ClassVisitor classVisitor) {
        super(ASM5, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        owner = name;
        isInterface = (access & ACC_INTERFACE) != 0;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);

        if (!isInterface && mv != null && !name.equals("<init>")) {
            AddTimerMethodAdapter at = new AddTimerMethodAdapter(mv);
            at.aa = new AnalyzerAdapter(owner, access, name, descriptor, at);
            at.lvs = new LocalVariablesSorter(access, descriptor, at.aa);

            return at.lvs;
        }

        return mv;
    }

    @Override
    public void visitEnd() {
        cv.visitEnd();
    }

    class AddTimerMethodAdapter extends MethodVisitor {
        private int time;
        private int maxStack;
        public LocalVariablesSorter lvs;
        public AnalyzerAdapter aa;

        public AddTimerMethodAdapter(MethodVisitor methodVisitor) {
            super(ASM5, methodVisitor);
        }


        @Override
        public void visitCode() {
            mv.visitCode();
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            time = lvs.newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, time);
            maxStack = 4;
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {

                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                mv.visitVarInsn(LLOAD, time);
                mv.visitInsn(LSUB);
                mv.visitVarInsn(LSTORE, time);

                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitVarInsn(LLOAD, time);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
                maxStack = Math.max(aa.stack.size() + 4, maxStack);
            }
            mv.visitInsn(opcode);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(Math.max(maxStack, this.maxStack), maxLocals);
        }
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, IOException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        TraceClassVisitor tv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        TimeCountAdpter addFiled = new TimeCountAdpter(tv);
        ClassReader classReader = new ClassReader("dynamic.asm.bean.Demo");
        classReader.accept(addFiled, ClassReader.EXPAND_FRAMES);

        File file = new File("dynamic/asm/bean/Demo.class");
        String parent = file.getParent();
        File parent1 = new File(parent);
        parent1.mkdirs();
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(cw.toByteArray());
    }
}
