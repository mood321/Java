package Annotation;

@User(name="Xiaoming")
public class DefaultUser {


    public static void main(String[] args) {
       User u = DefaultUser.class.getAnnotation(User.class);
        System.out.println(u.name()+ u.id());
    }

}
