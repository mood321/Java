package dynamic.asm;

import clazz.MyClassLoader;
import com.sun.xml.internal.ws.util.StringUtils;
import dynamic.asm.bean.Demo;
import dynamic.asm.bean.Log;
import jdk.internal.org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class MethodAddCode {
    public static class AddCodeVisitor extends ClassVisitor {

        public AddCodeVisitor(int i, ClassVisitor classVisitor) {
            super(i, classVisitor);
        }

        /**
         * 访问到方法时被调用
         * @param access
         * @param name
         * @param descriptor
         * @param signature
         * @param exceptions
         * @return
         */
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            //不代理构造函数
            if(!"<init>".equals(name)) {
                MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
                //方式一
                return new AddCodeMethodVisitor_1(this.api,mv);

                //方式二
                // return new AddCodeMethodVisitor_2(this.api,mv);

            }
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }

    /**
     * 方式一:通过字节编写字节码的方式织入代码
     */
    public static class AddCodeMethodVisitor_1 extends MethodVisitor {

        public AddCodeMethodVisitor_1(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        /**方法的开始,即刚进入方法里面*/
        @Override
        public void visitCode() {
            mv.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;");
            mv.visitLdcInsn("方式一:方法开始运行");
            mv.visitMethodInsn(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V");
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            if(opcode == ARETURN || opcode == RETURN ) {
                mv.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;");
                mv.visitLdcInsn("方式一:方法调用结束");
                mv.visitMethodInsn(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V");
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitEnd() {
            mv.visitMaxs(6,6);
            super.visitEnd();
        }
    }

    /**
     * 方式二:通过调用现有的class文件
     */
    public static class AddCodeMethodVisitor_2 extends MethodVisitor {

        public AddCodeMethodVisitor_2(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }


        /**方法的开始,即刚进入方法里面*/
        @Override
        public void visitCode() {
            mv.visitMethodInsn(INVOKESTATIC, Log.class.getName().replace(".","/"),"beforeMethod","()V",false);
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            if(opcode == ARETURN || opcode == RETURN ) {
                mv.visitMethodInsn(INVOKESTATIC,Log.class.getName().replace(".","/"),"afterMethod","()V",false);
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitEnd() {
            mv.visitMaxs(6,6);
            super.visitEnd();
        }
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String fullName = Demo.class.getName();
        String fullNameType = fullName.replace(".", "/");
        ClassReader cr = new ClassReader(fullNameType);
        ClassWriter cw = new ClassWriter(0);
        AddCodeVisitor cv = new AddCodeVisitor(Opcodes.ASM5,cw);
        cr.accept(cv,ClassReader.SKIP_DEBUG);
        byte[] bytes = cw.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(Demo.class.getClassLoader().getResource("dynamic/asm/bean/Demo.class").getFile());    // 将二进制流写到本地磁盘上
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
      /*  MyClassLoader classLoader = new MyClassLoader();
        Class<?> cls = classLoader.defineClassPublic(fullName, bytes, 0, bytes.length);*/
        try {
            Class cls = Class.forName("dynamic.asm.bean.Demo");

            Object o = cls.newInstance();
            Method getDemoInfo = cls.getMethod("getDemoInfo");
            getDemoInfo.invoke(o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
