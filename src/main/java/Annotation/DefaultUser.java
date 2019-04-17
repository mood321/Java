package Annotation;

import java.lang.reflect.Method;

@User(name="Xiaoming")
public class DefaultUser {

    @UserMethod(defaultName = "12")
    public void say(){
        System.out.println("DefaultUser.say");
    }

    public static void main(String[] args) throws NoSuchMethodException{
       User u = DefaultUser.class.getAnnotation(User.class);
        System.out.println(u.name()+ u.id());

        Method say = new DefaultUser().getClass().getMethod("say");

        UserMethod  method= say.getAnnotation(UserMethod.class);
        System.out.println(method.defaultName());
    }

}
