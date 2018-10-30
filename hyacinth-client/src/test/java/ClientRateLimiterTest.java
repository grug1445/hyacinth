import com.hyacinth.HyacinthRateLimiter;

/**
 * Created by feichen on 2018/10/30.
 */
public class ClientRateLimiterTest {
    public static void main(String[] args) {
        test("test1", 0, 6);
//        test("test2", 7, 20);
    }

    public static void test(String name, int start, int end) {
        for (int i = start; i < end; i++) {
            final int t = i;
            Thread thread = new Thread(() -> {
                HyacinthRateLimiter rateLimiter =
                        new HyacinthRateLimiter("localhost:2181", "hyacinth", 1.0, String.valueOf(t));
                while (true) {
                    System.out.println(name + "---client-----" + t + "----- acquire");
                    rateLimiter.acquire();
                }
            }, name + "-" + String.valueOf(t));
            thread.start();
        }
    }
}
