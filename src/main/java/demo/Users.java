package demo;

import java.util.TreeMap;

/**
 * @Created by mood321
 * @Date 2019/8/14 0014
 * @Description TODO
 */
public class Users implements  Comparable<Users> {
    int age=0;
    @Override
    public int compareTo(Users o) {
        return this.age-o.age;
    }

    public static void main(String[] args) {
        TreeMap<Users, String> map = new TreeMap<>();
        Users users = new Users();
        map.put(users,"12");
        map.put(new Users() ,"13");
        System.out.println(users);
        map.entrySet().stream().forEach((entry)->{

            System.out.println(entry.getKey()+"--"+map.get(entry.getKey()));
        });
    }
}
