package asm;

import jdk.internal.org.objectweb.asm.*;

import java.io.IOException;
import java.io.InputStream;

public class AsmTest {
    public static void main(String[] args)throws IOException {
  /*      InputStream inputStream = AsmTest.class.getClassLoader().getResources("Cat").getClass().getClassLoader().getResourceAsStream();
        ClassReader cr = new ClassReader(inputStream);//创建一个对象，接受一个被修改class类的inputStream，若只是生成class类传0
        ClassWriter cw = new ClassWriter(cr, 0);//创建一个修改class类的对象，接受ClassReader对象参数，在ClassWriter构造方法中保存了对象cr的引用
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {//通过内部类方式，真正的修改动作在重写方法中实现
            @Override
            public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
                return super.visitMethod(i, s, s1, s2, strings);
            }
        };
        cr.accept(cv, 0);//对象cr接受一个cv对象并完成class的修改
        cw.toByteArray();//返回class修改完后生成新的class字节流

*/
    }
}
