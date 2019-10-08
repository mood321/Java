package dynamic.asm.demo;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * 常用 API
 */
public class UtilApi {

    /**
     * Type 工具类
     * Type.getType(String.class).getInternalName()获取一个类的类名，只对类和接口有效。java/lang/String
     *
     * Type.getType(String.class).getDescriptor()获得一个类型的 描述符。Ljava/lang/String;
     *
     * Type.INT_TYPE.getDescriptor() 获取基本类型的描述符
     */
    public static  void TypeTest(){

        ClassWriter cw=new ClassWriter(3);
        TraceClassVisitor tv=new TraceClassVisitor(cw,new PrintWriter(System.out));

        tv.visit(V1_8,ACC_PUBLIC+ACC_ABSTRACT+ACC_INTERFACE,
                "org/by/Cwtest",null,"java/lang/Object",
                null);
        tv.visitField(ACC_PUBLIC+ACC_STATIC+ACC_FINAL,"LESS","I",
                null,new Integer(-1)).visitEnd();
        tv.visitField(ACC_PUBLIC+ACC_STATIC+ACC_FINAL,"EQUAL","I",
                null,new Integer(0)).visitEnd();
        tv.visitField(ACC_PUBLIC+ACC_STATIC+ACC_FINAL,"GRATER","I",
                null,new Integer(1)).visitEnd();

        tv.visitMethod(ACC_PUBLIC+ACC_ABSTRACT,"compareTo","(Ljava/lang/Object;)I",
                null,null).visitEnd();

        tv.visitEnd();
    }

    /**
     * CheckClassAdapter这类是用来检查它的方法调用以及参数是否正确
     */

    public static void CheckClassAdapter(){
        ClassWriter cw=new ClassWriter(3);
        CheckClassAdapter cca = new CheckClassAdapter(cw);
        TraceClassVisitor tv=new TraceClassVisitor(cca,new PrintWriter(System.out));

        tv.visit(V1_8,ACC_PUBLIC+ACC_ABSTRACT+ACC_INTERFACE,
                "org/by/Cwtest",null,"java/lang/Object",
                null);
        tv.visitField(ACC_PUBLIC+ACC_STATIC+ACC_FINAL,"LESS","I",
                null,new Integer(-1)).visitEnd();
        tv.visitField(ACC_PUBLIC+ACC_STATIC+ACC_FINAL,"EQUAL","I",
                null,new Integer(0)).visitEnd();
        tv.visitField(ACC_PUBLIC+ACC_STATIC+ACC_FINAL,"GRATER","I",
                null,new Integer(1)).visitEnd();

        tv.visitMethod(ACC_PUBLIC+ACC_ABSTRACT,"compareTo","(Ljava/lang/Object;)I",
                null,null).visitEnd();

        tv.visitEnd();
    }

    public static void main(String[] args) {
        CheckClassAdapter();
    }
}
