package top.xuqianvirtual.ReactiveStudy.demo;

import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

/**
 * 五、回压
 * 回压的作用是控制流量，这也是java stream没有的功能
 */
public class FluxBackPressTest {

    @Test
    public void testBackPress() {
        // 之前都没有进行流量控制，无论上游生成元素或快或慢，订阅者都会直接接收处理
        // 通过自定义Subscriber，来实现流量控制功能
        Flux.range(1, 6)
                // 打印每次请求的个数
                .doOnRequest(n -> System.out.println("Request " + n + " values..."))
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        // 定义在订阅时执行的操作，只会执行一次
                        System.out.println("Subscribed and make a request...");
                        // 此处若不请求，那么相当于没有订阅
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        // 定义收到一个元素时执行的操作，这里每秒消费一个元素
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Get value  [" + value + "]");
                        // 其实这里request传入的值只要大于0，无论是多少，都会一个一个打印
                        // 这里的request控制的是源头一次产生多少个元素
                        // 一般而言，建议设置为1，暂时没有考虑到会使用其他值的场景
                        request(1);
                    }
                });
    }
}
