package dynamic.asm.demo.tree;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 * 利用TreeApi 解析生成Class
 *
 * 前面CoreApi的介绍部分基本涵盖了ASMCore包下面的主要API及功能，其中还有一部分关于MetaData的解析和生成就不再赘述。这篇开始介绍ASM另一部分主要的Api。TreeApi。
 * 这一部分源码是关联的asm-tree-5.0.4的版本。
 *
 * 在介绍前，先要知道一点， Tree工程的接口基本可以完成大部分我们之前介绍的Core中的功能。但是在实际使用中更加便利，当然也会更加消耗时间和性能。
 * 完成一个简单的生成编译后的Class字节码的任务，可能会花费多余Core的30%的时间，同时也会消耗更多内存。但是通过下面的介绍，相信在选择用哪种Api上，
 * 我们也会做出自己的取舍和判断
 */
public class GenerateClasses {

    public static void main(String[] args) throws FileNotFoundException {
        ClassWriter cw = new ClassWriter(Opcodes.ASM5);
        ClassNode cn = gen();
        cn.accept(cw);
        File file = new File("org/by/Cwtest.class");
        FileOutputStream fout = new FileOutputStream(file);
        try {
            fout.write(cw.toByteArray());
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *  解析class 文件
     * @return
     */
    private static ClassNode gen() {
        ClassNode classNode = new ClassNode();
        classNode.version = Opcodes.V1_8;
        classNode.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT;
        classNode.name = "asm/core/ChildClass";
        classNode.superName = "java/lang/Object";
        classNode.interfaces.add("asm/core/ParentInter");
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "zero", "I",
                null, new Integer(0)));
        classNode.methods.add(new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "compareTo",
                "(Ljava/lang/Object;)I", null, null));
        return classNode;

    }

    /**
     *  去除某个方法
     * @param cn
     * @param fieldName
     */
    public  void RemoveMethodTransformer(ClassNode cn , String fieldName) {

        Iterator<MethodNode> i = cn.methods.iterator();
        while (i.hasNext()) {
            MethodNode mn = i.next();
            if (mn.name.equals(fieldName) ) {
                i.remove();
            }
        }
    }

    /**
     *  添加 某个字段
     * @param cn
     * @param fieldAccess
     * @param fieldName
     * @param fieldDesc
     */
    public void AddFieldTransformer(ClassNode cn,int fieldAccess, String fieldName, String fieldDesc) {

        boolean isPresent = false;
        for (Object fn : cn.fields) {
            FieldNode ff = (FieldNode) fn;
            if (fieldName.equals(ff.name)) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            cn.fields.add(new FieldNode(fieldAccess, fieldName, fieldDesc, null, null));
        }
    }

}
