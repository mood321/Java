package clazz;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * @author  mood321
 *
 *  语言层面的方法动态调用
 *  MethodHandles.lookup 中 3 个方法对应的字节码指令：
 *
 * findStatic()：对应 invokestatic
 * findVirtual()：对应 invokevirtual & invokeinterface
 * findSpecial()：对应 invokespecial
 * MethodHandle 和 Reflection 的区别
 *
 * Java 虚拟机提供了 5 个职责不同的方法调用字节码指令：
 *
 * invokestatic：调用静态方法；
 * invokespecial：调用构造器方法、私有方法、父类方法；
 * invokevirtual：调用所有虚方法，除了静态方法、构造器方法、私有方法、父类方法、final 方法的其他方法叫虚方法；
 * invokeinterface：调用接口方法，会在运行时确定一个该接口的实现对象；
 * invokedynamic：在运行时动态解析出调用点限定符引用的方法，再执行该方法。
 * 
 * 本质区别： 它们都在模拟方法调用，但是
 * Reflection 模拟的是 Java 代码层次的调用；
 * MethodHandle 模拟的是字节码层次的调用。
 * 包含信息的区别：
 * Reflection 的 Method 对象包含的信息多，包括：方法签名、方法描述符、方法的各种属性的Java端表达方式、方法执行权限等；
 * MethodHandle 对象包含的信息比较少，既包含与执行该方法相关的信息。
 */
public class MethodHandleDemo {
    static class ClassA {
        public void println(String s) {
            System.out.println(s);
        }
    }

    public static void main(String[] args) throws Throwable {
        /*
        obj的静态类型是Object，是没有println方法的，所以尽管obj的实际类型都包含println方法，
        它还是不能调用println方法
         */
        Object obj = System.currentTimeMillis() % 2 == 0 ? System.out : new ClassA();
        /*
        invoke()和invokeExact()的区别：
        - invokeExact()要求更严格，要求严格的类型匹配，方法的返回值类型也在考虑范围之内
        - invoke()允许更加松散的调用方式
         */
        getPrintlnMH(obj).invoke("Hello world");
        getPrintlnMH(obj).invokeExact("Hello world");
    }

    private static MethodHandle getPrintlnMH(Object receiver)
            throws NoSuchMethodException, IllegalAccessException {
        /* MethodType代表方法类型，第一个参数是方法返回值的类型，之后的参数是方法的入参 */
        MethodType mt = MethodType.methodType(void.class, String.class);
        /*
        lookup()方法来自于MethodHandles.lookup，
        这句的作用是在指定类中查找符合给定的方法名称、方法类型，并且符合调用权限的方法句柄
        */
        /*
        因为这里调用的是一个虚方法，按照Java语言的规则，方法第一个参数是隐式的，代表该方法的接收者，
        也即是this指向的对象，这个参数以前是放在参数列表中进行传递，现在提供了bindTo()方法来完成这件事情
        */
        return MethodHandles.lookup().findVirtual(receiver.getClass(), "println", mt).bindTo(receiver);
    }
}
