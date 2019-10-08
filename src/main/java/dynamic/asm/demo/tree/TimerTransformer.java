package dynamic.asm.demo.tree;

import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

/**
 * 利用TreeApi动态注入方法逻辑
 */
public class TimerTransformer {

    public void addTransform(ClassNode cn) {
        for (MethodNode mn : (List<MethodNode>) cn.methods) {
            if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                continue;
            }
            InsnList insns = mn.instructions;
            if (insns.size() == 0) {
                continue;
            }
            Iterator<AbstractInsnNode> j = insns.iterator();
            while (j.hasNext()) {
                AbstractInsnNode in = j.next();
                int op = in.getOpcode();
                if ((op >= Opcodes.IRETURN && op <= Opcodes.RETURN) || op == Opcodes.ATHROW) {
                    InsnList il = new InsnList();
                    il.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, "timer", "J"));
                    il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J",
                            false));
                    il.add(new InsnNode(Opcodes.LADD));
                    il.add(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, "timer", "J"));
                    insns.insert(in.getPrevious(), il);
                }
            }
            InsnList il = new InsnList();
            il.add(new FieldInsnNode(Opcodes.GETSTATIC, cn.name, "timer", "J"));
            il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
            il.add(new InsnNode(Opcodes.LSUB));
            il.add(new FieldInsnNode(Opcodes.PUTSTATIC, cn.name, "timer", "J"));
            insns.insert(il);
            mn.maxStack += 4;
        }
        int acc = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;
        cn.fields.add(new FieldNode(acc, "timer", "J", null, null));
    }

    /**
     * 将GOTO label 替换成label实际跳转到的指令-RETURN
     */
    public void transform(MethodNode mn) {
        InsnList insns = mn.instructions;
        Iterator<AbstractInsnNode> i = insns.iterator();
        while (i.hasNext()) {
            AbstractInsnNode in = i.next();
            if (in instanceof JumpInsnNode) {
                // 初始化label
                LabelNode label = ((JumpInsnNode) in).label;
                AbstractInsnNode target;
                // 循环调用，将goto XX 中的XX跳转地址记录在label变量中
                while (true) {
                    target = label;   // 跳转过滤掉FrameNode 和LabelNode
                    while (target != null && target.getOpcode() < 0) {
                        target = target.getNext();
                    }
                    if (target != null && target.getOpcode() == Opcodes.GOTO) {
                        label = ((JumpInsnNode) target).label;
                    } else {
                        break;
                    }
                }
                // 更新替换label的值(实际跳转地址)
                ((JumpInsnNode) in).label = label;
                // 如果指令是goto ,并且新的跳转的目标指令是ARETURN 指令，那么就将当前的指令替换成这个return指令的一个clone对象
                if (in.getOpcode() == Opcodes.GOTO && target != null) {
                    int op = target.getOpcode();
                    if ((op >= Opcodes.IRETURN && op <= Opcodes.RETURN) || op == Opcodes.ATHROW) {
                        // replace ’in’ with clone of ’target’
                        insns.set(in, target.clone(null));
                    }
                }
            }
        }
    }

}
