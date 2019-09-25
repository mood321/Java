package dynamic.javassit;

import clazz.MyClassLoader;
import dynamic.asm.bean.Demo;
import javassist.*;
import javassist.bytecode.AccessFlag;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JavassitDemo {
    public static void main(String[] args) throws Exception {
        // 动态创建一个类 和基础Api
         classAdd();
         //工厂 方式代理
        //   testJavassistFactoryProxy();
        // 动态代理
       // testJavassistDefineClass();

        // 修改原方法
        // modifyMethod();
    }


    /**
     * 动态创建一个新的Class 基础Api
     */
    private static void classAdd() {
        ClassPool pool = ClassPool.getDefault();
        CtClass ct = pool.makeClass("dynamic.asm.bean.javassitDemo");//创建类
        ct.setInterfaces(new CtClass[]{pool.makeInterface("java.lang.Cloneable")});//让类实现Cloneable接口
        try {
            CtField f = new CtField(CtClass.intType, "id", ct);//获得一个类型为int，名称为id的字段
            f.setModifiers(AccessFlag.PUBLIC);//将字段设置为public
            ct.addField(f);//将字段设置到类上
            //默认构造
            ct.addConstructor(CtNewConstructor.defaultConstructor(ct));
            //添加构造函数
            CtConstructor constructor = CtNewConstructor.make("public javassitDemo(int pId){this.id=pId;}", ct);
            ct.addConstructor(constructor);
            //添加方法
            CtMethod helloM = CtNewMethod.make("public static void hello(String des){ System.out.println(des);}", ct);
            ct.addMethod(helloM);
            ct.writeFile();//将生成的.class文件保存到磁盘

            //下面的代码为验证代码
            Class<?> clazz = ct.toClass();
            Field[] fields = clazz.getFields();
            System.out.println(clazz);
            System.out.println("属性名称：" + fields[0].getName() + "  属性类型：" + fields[0].getType());
            CtMethod[] methods = ct.getMethods();
            for (CtMethod methos : methods) {
                System.out.println(methos.getName());

            }
            // 默认构造方法
            //clazz.newInstance();
            //任意构造器
            Constructor<?> constructor1 = clazz.getConstructor(new Class[]{int.class});
            Object instance = constructor1.newInstance(1);
            System.out.println(instance);
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } /*catch (IOException e) {
            e.printStackTrace();
        }*/ catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 代理工厂创建动态代理
    public static void testJavassistFactoryProxy() throws Exception {
        // 创建代理工厂
        ProxyFactory proxyFactory = new ProxyFactory();

        // 设置被代理类的类型
        proxyFactory.setSuperclass(Demo.class);

        // 创建代理类的class
        Class proxyClass = proxyFactory.createClass();

        // 创建对象
        Demo proxyTest = (Demo) proxyClass.newInstance();

        ((ProxyObject) proxyTest).setHandler(new MethodHandler() {
            // 真实主题
            Demo test = new Demo();

            @Override
            public Object invoke(Object self, Method thisMethod,
                                 Method proceed, Object[] args) throws Throwable {

                System.out.println("before ");
                Object str = thisMethod.invoke(test, args);
                System.out.println("after ");
                return null;
            }
        });
        proxyTest.getDemoInfo();

    }


    // 动态代码创建的例子
    // 下面例子使用 Javassist 的 API成功组织出代理类的一个子类，可以看出 添加构造函数，添加属性，
    // 添加方法，内容 都是通过字符串类型完成即可。 通过 Javassist 强大的字节生成能力可以达到动态
    // 增加类和实现动态代理的功能.
    public static void testJavassistDefineClass() throws Exception {
        // 创建类池，true 表示使用默认路径
        ClassPool classPool = new ClassPool(true);

        String className = Demo.class.getName();
        // 创建一个类 RayTestJavassistProxy
        CtClass ctClass = classPool.makeClass(className + "JavassistProxy");

        // 添加超类
        // 设置 RayTestJavassistProxy 的父类是 Demo.
        ctClass.setSuperclass(classPool.get(Demo.class.getName()));

        // 添加默认构造函数
        ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));

        // 添加属性
        ctClass.addField(CtField.make("public " + className + " real = new " +
                className + "();", ctClass));

        // 添加方法，里面进行动态代理 getDemoInfo
        ctClass.addMethod(CtNewMethod.make("public void getDemoInfo() { " +
                        " System.out.println(\"before \");" +
                        " real.getDemoInfo();" +
                        " System.out.println(\"after \");" +

                        "return  ;}",
                ctClass));
        Class testClass = ctClass.toClass();
        Demo rayTest = (Demo) testClass.newInstance();
        rayTest.getDemoInfo();

    }

    /**
     * 内置 方法实现aop
     */
    public static void modifyMethod() {
        ClassPool pool = ClassPool.getDefault();
        try {

            CtClass ct = pool.getCtClass("dynamic.asm.bean.Demo");
            CtMethod m = ct.getDeclaredMethod("getDemoInfo");

            m.insertBefore("{ System.out.println(\"before \"); }");
            m.insertAfter("{ System.out.println(\"after \"); }");

            ct.writeFile();
            //通过反射调用方法，查看结果
            Class pc = ct.toClass();//new MyClassLoader().findClass(ct.getName());
            Method move = pc.getMethod("getDemoInfo");

            move.invoke(pc.newInstance());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
