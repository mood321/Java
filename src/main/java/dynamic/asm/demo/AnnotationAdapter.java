package dynamic.asm.demo;

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

import static org.objectweb.asm.Opcodes.ASM6;

/**
 * 注解
 * 只要一个注解的作用域不是RetentionPolicy.SOURCE，那么它就会存储在编译后的class文件中。如果作用域是RetentionPolicy.RUNTIME，那么可以在运行时被反射获取。
 *
 * ASM框架通过AnnotationVisitor来访问和修改注解。
 *
 * 访问顺序：
 *
 * visitAnnotation方法返回Null可以移除一个注解，
 * 下面分别移除了 字段上的注解，类上的注解，以及获取注解的值。
 */
public class AnnotationAdapter extends ClassVisitor implements Opcodes {
    public AnnotationAdapter(ClassVisitor classVisitor) {
        super(ASM5, classVisitor);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        FieldVisitor fv= super.visitField(access, name, descriptor, signature, value);
        if (fv!=null)
            fv=new RemoveFieldAnnotation(fv);//移除一个注解
        return fv;
    }

    /**
     * 移除类上的ClassR1注解
     * @param descriptor
     * @param visible
     * @return
     */
    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (descriptor.equals("Lcom/liu/asm/ClassR1;")){
            return null;
        }
        return new AnnotationValueVisitor(super.visitAnnotation(descriptor, visible));
    }

    public static void main(String[] args) throws IOException {
        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);
        TraceClassVisitor tv=new TraceClassVisitor(cw,new PrintWriter(System.out));
        AnnotationAdapter addFiled=new AnnotationAdapter(tv);
        ClassReader classReader=new ClassReader("dynamic.asm.bean.Demo");
        classReader.accept(addFiled,ClassReader.EXPAND_FRAMES);
    }
    class RemoveFieldAnnotation extends FieldVisitor{

        public RemoveFieldAnnotation(FieldVisitor fieldVisitor) {
            super(ASM5,fieldVisitor);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals("Lcom/liu/asm/ClassR1;")){
                return null;
            }
            return new AnnotationValueVisitor(super.visitAnnotation(descriptor, visible));
        }
    }
    class AnnotationValueVisitor extends AnnotationVisitor{

        public AnnotationValueVisitor( AnnotationVisitor annotationVisitor) {
            super(ASM5, annotationVisitor);
        }

        @Override
        public void visit(String name, Object value) {
            System.out.println("注解"+name+value);
            super.visit(name, value);
        }
    }
}
