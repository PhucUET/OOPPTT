package unknown.oopptt.api;

public class TestAPI {
    public static void main(String[] args) {
        Data data = new Data();

        //Test đăng ký
        System.out.println("Test đăng ký:");
        System.out.println(data.register("thuy", "123")); // REGISTER_OK hoặc EXISTS

        //Test đăng nhập
//        System.out.println("Test đăng nhập:");
//        System.out.println(data.login("thuythin", "Thuythin2006@")); // LOGIN_OK hoặc INVALID
    }
}
