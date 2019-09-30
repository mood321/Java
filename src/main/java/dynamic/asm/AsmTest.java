package dynamic.asm;

import clazz.MyClassLoader;
import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class AsmTest {
    public static void main(String[] args)throws IOException {
      InputStream inputStream = AsmTest.class.getResourceAsStream("Cat.class");
        ClassReader cr = new ClassReader(inputStream);//创建一个对象，接受一个被修改class类的inputStream，若只是生成class类传0
        ClassWriter cw = new ClassWriter(cr, 0);//创建一个修改class类的对象，接受ClassReader对象参数，在ClassWriter构造方法中保存了对象cr的引用

       ClassVisitor cv = new ChangeVisitor( cw);
       // cr.accept(cv, ClassReader.EXPAND_FRAMES);//对象cr接受一个cv对象并完成class的修改
        cr.accept( cv, ClassReader.SKIP_DEBUG);
        byte[] code =   cw.toByteArray();//返回class修改完后生成新的class字节流  // 获取修改后的 class 文件对应的字节数组
        try {
            FileOutputStream fos = new FileOutputStream(AsmTest.class.getResource("Cat.class").getFile());    // 将二进制流写到本地磁盘上
            fos.write(code);
            fos.close();

            Class clazz = Class.forName("dynamic.asm.Cat");
            Object personObj = clazz.newInstance();
            Method nameMethod = clazz.getDeclaredMethod("sayHello", null);
            nameMethod.invoke(personObj, null);
            System.out.println("Success!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    static class ChangeVisitor extends ClassVisitor {

        ChangeVisitor(ClassVisitor classVisitor) {
            super(Opcodes.ASM4, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            if (name.equals("<init>")) {
                return methodVisitor;
            }
            MethodVisitor superMV = super.visitMethod(access, name, desc, signature, exceptions);
            return new DemoMethodVisitor(superMV,  name);
        }
    }

    static class ChangeAdapter extends AdviceAdapter {
        private int startTimeId = -1;

        private String methodName = null;

        ChangeAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv, access, name, desc);
            methodName = name;
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
            startTimeId = newLocal(Type.LONG_TYPE);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitIntInsn(LSTORE, startTimeId);
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            int durationId = newLocal(Type.LONG_TYPE);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitVarInsn(LLOAD, startTimeId);
            mv.visitInsn(LSUB);
            mv.visitVarInsn(LSTORE, durationId);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitLdcInsn("The cost time of " + methodName + " is ");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(LLOAD, durationId);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }
   static class DemoMethodVisitor extends MethodVisitor {
       private String methodName;

       public DemoMethodVisitor(MethodVisitor mv, String methodName) {
           super(Opcodes.ASM4, mv);
           this.methodName = methodName;
       }

       public void visitCode() {
           System.out.println("at Method ‘" + methodName + "’ Begin...");
           super.visitCode();
       }

       public void visitEnd() {
           System.out.println("at Method ‘" + methodName + "’End.");
           super.visitEnd();
       }
   }
}
