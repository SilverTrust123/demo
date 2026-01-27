import java.net.Socket;

public class TestTCP {
    public static void main(String[] args) {
        try (Socket s = new Socket("192.168.3.20", 502)) {
            System.out.println("連線成功");
        } catch (Exception e) {
            System.out.println("連線失敗：" + e.getMessage());
        }
    }
}
