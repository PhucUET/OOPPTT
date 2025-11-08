package unknown.oopptt.api;

public class TestAPI {
    public static void main(String[] args) {
        Data data = new Data();

        //Test đăng ký
//        System.out.println("Test đăng ký:");
//        System.out.println(data.register("tester01", "12345")); // REGISTER_OK hoặc EXISTS

        //Test đăng nhập
        System.out.println("Test đăng nhập:");
        System.out.println(data.login("tester01", "12346")); // LOGIN_OK hoặc INVALID
    }
}
