package dynamic.asm.demo;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 移除类成员
 *
 * 下面的示例，移除了类Person中do开头的方法，并修改了类名Person为Cwtest。
 *
 * 如果想移除某个方法，只需要返回Null（也就是不返回method）
 */
public class RemoveMethod extends ClassVisitor {
    public RemoveMethod(ClassWriter cw) {
        super(Opcodes.ASM5,cw);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, "dynamic/asm/bean/Demo", signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.startsWith("main")){
            return null;
        }
        return cv.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public static void main(String[] args) throws IOException {
        ClassWriter classWriter=new ClassWriter(3);
        RemoveMethod removeMethod=new RemoveMethod(classWriter);
        ClassReader classReader=new ClassReader("dynamic.asm.bean.Demo");
        classReader.accept(removeMethod,0);

        File file=new File("org/by/Cwtest.class");
        String parent=file.getParent();
        File parent1=new File(parent);
        parent1.mkdirs();
        file.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        fileOutputStream.write(classWriter.toByteArray());
    }
}
