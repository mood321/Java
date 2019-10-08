package dynamic.asm.demo;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author  mood321
 * 添加类成员，比如添加一个字段。为了确保字段名不重复，添加字段的操作，在访问了所有的字段信息之后执行
 */
public class AddFiled extends ClassVisitor {
    String filedName="thisIsNewAddF1";
    private int acc= Opcodes.ACC_PUBLIC;
    boolean isPresent=false;
    public AddFiled( ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (name.equals(filedName)){
            isPresent=true;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
        if (!isPresent){
            //没有这个字段
            FieldVisitor fv= this.cv.visitField(acc,filedName,"I",null,3);
            if (fv!=null){
                fv.visitEnd();
            }
        }
        super.visitEnd();
    }

    public static void main(String[] args) throws IOException {
        ClassWriter classWriter=new ClassWriter(3);
        AddFiled addFiled=new AddFiled(classWriter);
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
