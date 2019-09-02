package top.xuqianvirtual.ReactiveStudy.demo.model;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

import javax.validation.constraints.NotNull;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 慢的订阅者
 */
public class SlowSubscriber extends BaseSubscriber<MyEvent> {

    // 订阅者处理每个元素的时间，单位毫秒
    private final int PROCESS_DURATION = 30;
    private CountDownLatch countDownLatch;

    public SlowSubscriber(@NotNull CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        // 订阅时请求一个元素
        request(1);
    }

    @Override
    protected void hookOnNext(MyEvent event) {
        System.out.println("    recive <<< " + event.getMessage());
        try {
            TimeUnit.MILLISECONDS.sleep(PROCESS_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        request(1);
    }

    @Override
    protected void hookOnError(Throwable throwable) {
        System.err.println("    receive <<< " + throwable);
    }

    @Override
    protected void hookOnComplete() {
        countDownLatch.countDown();
    }
}
