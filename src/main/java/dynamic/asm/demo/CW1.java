package dynamic.asm.demo;

import jdk.internal.org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class CW1 {
    public static void main(String[] args) throws IOException {
        ClassWriter cw=new ClassWriter(0);
        //类版本，访问标志以及修饰符，类全名，泛型，父类，接口
        cw.visit(V1_7,ACC_PUBLIC+ACC_ABSTRACT+ACC_INTERFACE,
                "org/by/Cwtest",null,"java/lang/Object",
                new String[]{"org/by/ICw"});
        //访问标志，名字，类型，泛型，值
        cw.visitField(ACC_PUBLIC+ACC_STATIC+ACC_FINAL,"LESS","I",
                null,new Integer(-1)).visitEnd();
        cw.visitField(ACC_PUBLIC+ACC_STATIC+ACC_FINAL,"EQUAL","I",
                null,new Integer(0)).visitEnd();
        cw.visitField(ACC_PUBLIC+ACC_STATIC+ACC_FINAL,"GRATER","I",
                null,new Integer(1)).visitEnd();
        //访问标志，名字，签名，泛型，throws异常
        cw.visitMethod(ACC_PUBLIC+ACC_ABSTRACT,"compareTo","(Ljava/lang/Object;)I",
                null,null).visitEnd();

        cw.visitEnd();//通知classWriter，类定义完成了
        String systemRootUrl = (new File("")).toURI().toURL().getPath();
        File file=new File(systemRootUrl+"org/by/Cwtest.class");
        String parent=file.getParent();
        File parent1=new File(parent);
        parent1.mkdirs();
        file.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        fileOutputStream.write(cw.toByteArray());
    }
}
