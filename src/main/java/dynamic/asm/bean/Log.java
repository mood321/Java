package dynamic.asm.bean;

public class Log {
    public static final void beforeMethod() {
        System.out.println("方式二:方法开始运行...");
    }

    public static final void afterMethod() {
        System.out.println("方式二:方法运行结束...");
    }

}
