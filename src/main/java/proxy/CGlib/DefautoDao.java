package proxy.CGlib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class DefautoDao implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("Befor");
        methodProxy.invokeSuper(o,objects);
        System.out.println("after");
        return o;
    }

    public static void main(String[] args) {
        //
        DefautoDao daoProxy = new DefautoDao();

        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(CglibInterface.class);
        enhancer.setCallback(daoProxy);
        CglibInterface o = (CglibInterface)enhancer.create();

        o.select();
    }
}
