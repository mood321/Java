package dynamic.javassit;

import dynamic.asm.bean.Demo;
import javassist.*;
import javassist.bytecode.AccessFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JavassitDemo {
    public static void main(String[] args) {
    // classAdd();
getMethos(Demo.class);
    }

    private static void getMethos(Class clazz) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ct = pool.makeClass(clazz.getName());
        CtMethod[] methods = ct.getMethods();
       for (CtMethod methos:methods){
           System.out.println(methos.getName());

       }

    }

    private static void classAdd() {
        ClassPool pool = ClassPool.getDefault();
        CtClass ct = pool.makeClass("dynamic.asm.bean.javassitDemo");//创建类
        ct.setInterfaces(new CtClass[]{pool.makeInterface("java.lang.Cloneable")});//让类实现Cloneable接口
        try {
            CtField f= new CtField(CtClass.intType,"id",ct);//获得一个类型为int，名称为id的字段
            f.setModifiers(AccessFlag.PUBLIC);//将字段设置为public
            ct.addField(f);//将字段设置到类上
            //添加构造函数
            CtConstructor constructor= CtNewConstructor.make("public GeneratedClass(int pId){this.id=pId;}",ct);
            ct.addConstructor(constructor);
            //添加方法
            CtMethod helloM=CtNewMethod.make("public void hello(String des){ System.out.println(des);}",ct);
            ct.addMethod(helloM);
            ct.writeFile();//将生成的.class文件保存到磁盘

            //下面的代码为验证代码
            Class<?> clazz = ct.toClass();
            Field[] fields = clazz.getFields();
            System.out.println(clazz);
            System.out.println("属性名称：" + fields[0].getName() + "  属性类型：" + fields[0].getType());
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
