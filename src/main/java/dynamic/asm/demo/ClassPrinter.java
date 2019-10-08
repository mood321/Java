package dynamic.asm.demo;

import dynamic.asm.bean.Demo;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;

/**
 * classVistor像一个过滤器，它通过委托调用传递给它的classVistor实例。
 *
 * classreader用来解析一个已存在的类。
 * 下面是模仿javap打印一个类的信息，这里classReader承担生产者的角色，ClassPrinter作为消费者。
 */
public class ClassPrinter extends ClassVisitor {

    public ClassPrinter() {
        super(Opcodes.ASM5);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println(name+" extend "+superName +" implements "+interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        System.out.println(" "+descriptor+" "+name);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println(" "+name+descriptor);
        return null;
    }

    public static void main(String[] args) throws IOException {
        ClassPrinter classPrinter=new ClassPrinter();

        InputStream cl=ClassLoader.getSystemResourceAsStream(Demo.class.getName().replace(".","/")+".class");
        ClassReader classReader=new ClassReader(cl);

        classReader.accept(classPrinter,0);

    }
}

