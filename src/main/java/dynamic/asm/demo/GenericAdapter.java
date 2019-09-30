package dynamic.asm.demo;

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.signature.SignatureReader;
import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;
import jdk.internal.org.objectweb.asm.signature.SignatureWriter;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

import static org.objectweb.asm.Opcodes.ASM6;

/**
 * 对于泛型类型Type的解析，必须按照下面的顺序
 * 访问方法签名按照下面的顺序
 * 访问类签名的顺序
 * 其中大部分方法都会返回一个SignatureVisitor，用于访问一个type签名。
 * SignatureVisitor返回SignatureVisitors（不能返回null）。
 *
 * ASM提供SignatureReader和SugnatureWriter用于解析和创建签名。
 * SignatureReader解析签名并调用SignatureVisitor中相应的方法，SugnatureWriter根据收到的方法调用创建签名。
 *
 * public class Node<T> {
 *     int key;
 *     T value;
 *
 *     public <K>void do1(K a){
 *         System.out.println( "ss1"+a);
 *     }
 *
 *     public static void main(String[] args) {
 *         Node m=new Node();
 *         m.do1(4);
 *     }
 * }
 * 给Node类添加一个泛型M,也给方法添加一个泛型声明M：
 */
public class GenericAdapter extends ClassVisitor implements Opcodes {
    public GenericAdapter( ClassVisitor classVisitor) {
        super(ASM5, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        if (s2!=null) {
            s2=addGernicM(s2);
        }
        return super.visitMethod(i, s, s1, s2, strings);
    }

    @Override
    public void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
        if (s1!=null)
            s1=addGernicM(s1);
        super.visit(i, i1, s, s1, s2, strings);
    }

    private String addGernicM(String s1) {
        SignatureWriter sw = new SignatureWriter();
        SignatureVisitor sa = new AddGernicMVisiter(sw);
        SignatureReader sr = new SignatureReader(s1);
        sr.acceptType(sa);
        return sw.toString();
    }

    public static void main(String[] args) throws IOException {
        ClassReader classReader=new ClassReader("dynamic.asm.bean.Demo");
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);
        TraceClassVisitor traceClassVisitor=new TraceClassVisitor(cw,new PrintWriter(System.out));
        GenericAdapter genericAdapter=new GenericAdapter(traceClassVisitor);
        classReader.accept(genericAdapter,ClassReader.EXPAND_FRAMES);

    }
    class AddGernicMVisiter extends SignatureVisitor{
        private SignatureVisitor signatureVisitor;
        public AddGernicMVisiter(SignatureVisitor signatureVisitor) {
            super(ASM6);
            this.signatureVisitor=signatureVisitor;
        }


        @Override
        public void visitClassType(String s) {
            signatureVisitor.visitClassType(s+"M:Ljava/lang/Object;");
        }

    }

}