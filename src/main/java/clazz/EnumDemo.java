package clazz;

public enum EnumDemo {
    DEMO1("1") {
        @Override
        void run() {
            System.out.println("zheshi 1");
        }
    };
    private String code;

    abstract void run();

    EnumDemo(String code) {
        this.code = code;
    }

    public static void main(String[] args) {
        EnumDemo demo = EnumDemo.valueOf("DEMO1");
        demo.run();
    }
}
