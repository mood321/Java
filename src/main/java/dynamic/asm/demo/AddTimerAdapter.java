package dynamic.asm.demo;

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.util.ASMifier;

import java.io.File;
import java.io.FileOutputStream;

import static jdk.internal.org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ASM4;

/**
 * ASMifier用来产生asm代码，当有一个类时，使用这个类读取目标类，会产生产生这个字节码所需要的asm代码。如果不知道怎么写ASM代码，可以先写Java源码，然后用这个类产生相应的代码。
 * 调用方式：
 *
 *    ASMifier.main(new String[]{"com.liu.hash.DataItem"});
 * 1
 * Opcodes可以分为2类：一类把局部变量表的值压栈，另一类只作用于操作数栈，计算，出栈入栈。
 *
 * 修改方法必须执行的顺序
 *
 * visitAnnotationDefault?
 * ( visitAnnotation | visitParameterAnnotation | visitAttribute )*
 * ( visitCode
 * 	( visitTryCatchBlock | visitLabel | visitFrame | visitXxxInsn |
 * 		visitLocalVariable | visitLineNumber )*
 * 	visitMaxs )?
 * visitEnd
 */
public class AddTimerAdapter extends ClassVisitor {
    private String owner;
    private boolean isInterface;
    public AddTimerAdapter(ClassVisitor cv) {
        super(ASM4, cv);
    }
    @Override public void visit(int version, int access, String name,
                                String signature, String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
        owner = name;
        isInterface = (access & ACC_INTERFACE) != 0;
    }
    @Override public MethodVisitor visitMethod(int access, String name,
                                               String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
                exceptions);
        if (!isInterface && mv != null && !name.equals("<init>")) {
            mv = new AddTimerMethodAdapter(mv);
        }
        return mv;
    }
    @Override public void visitEnd() {
        if (!isInterface) {
            FieldVisitor fv = cv.visitField(ACC_PUBLIC + ACC_STATIC, "timer",
                    "J", null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        cv.visitEnd();
    }
    class AddTimerMethodAdapter extends MethodVisitor {
        public AddTimerMethodAdapter(MethodVisitor mv) {
            super(ASM4, mv);

        }
        @Override public void visitCode() {
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, owner, "timer", "J");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System",
                    "currentTimeMillis", "()J");
            mv.visitInsn(LSUB);
            mv.visitFieldInsn(PUTSTATIC, owner, "timer", "J");
        }
        @Override public void visitInsn(int opcode) {
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                mv.visitFieldInsn(GETSTATIC, owner, "timer", "J");
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System",
                        "currentTimeMillis", "()J");
                mv.visitInsn(LADD);
                mv.visitFieldInsn(PUTSTATIC, owner, "timer", "J");
            }
            mv.visitInsn(opcode);
        }
        @Override public void visitMaxs(int maxStack, int maxLocals) {
            mv.visitMaxs(maxStack + 4, maxLocals);
        }
    }

   public static void main(String[] args) throws  Exception{
       ClassWriter classWriter=new ClassWriter(3);
       AddTimerAdapter addFiled=new AddTimerAdapter(classWriter);
       ClassReader classReader=new ClassReader("dynamic.asm.bean.Demo");
       classReader.accept(addFiled,0);

       File file=new File("org/by/Cwtest.class");
       String parent=file.getParent();
       File parent1=new File(parent);
       parent1.mkdirs();
       file.createNewFile();
       FileOutputStream fileOutputStream=new FileOutputStream(file);
       fileOutputStream.write(classWriter.toByteArray());
    }
}

