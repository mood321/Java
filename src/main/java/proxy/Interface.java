package proxy;

public interface Interface {
    public void sayHi();
    public void sayHello();
}

class DefaultInterface implements Interface
{

    @Override
    public void sayHi() {
        System.out.println("hi");
    }

    @Override
    public void sayHello() {
        System.out.println("hello");
    }
}
