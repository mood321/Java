package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Proxy implements InvocationHandler {

    private Object proxyed;

    public Proxy(Object proxy) {
        this.proxyed = proxy;
    }


    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("defaultProxy is  run");
        return method.invoke(proxyed);
    }

    public static void main(String[] args) {
        DefaultInterface defaultInterface = new DefaultInterface();
        Proxy defaultProxy = new Proxy(defaultInterface);

        Interface o =(Interface) java.lang.reflect.Proxy.newProxyInstance(Interface.class.getClassLoader(), new Class[]{Interface.class}, new Proxy(defaultInterface));

        o.sayHi();
    }

}
