package top.xuqianvirtual.ReactiveStudy.demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import top.xuqianvirtual.ReactiveStudy.demo.model.MyEvent;
import top.xuqianvirtual.ReactiveStudy.demo.model.MyEventListener;
import top.xuqianvirtual.ReactiveStudy.demo.model.MyEventSource;
import top.xuqianvirtual.ReactiveStudy.demo.model.SlowSubscriber;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 五、回压
 * 回压的作用是控制流量，这也是java stream没有的功能
 */
public class FluxBackPressTest {

    // 在同一线程内使用的回压方式
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

    // 发布者与订阅者不在同一线程的情况
    // 并且发布者发出数据的速度高于订阅者处理数据的速度
    // 有以下几种回压策略：
    // 1.ERROR:当下游跟不上节奏的时候发出一个错误信号
    // 2.DROP:当下游没有准备好接受新的元素时抛弃这个元素
    // 3.LATEST:让下游只得到上游最新的元素
    // 4.BUFFER:缓存下游们没有来得及处理的元素（注意需要设置缓存上限）
    // 5.IGNORE:忽略下游的回压请求，容易出现下游队列积满而报异常

    // 使用create声明回压策略

    // 生成的事件间隔事件，单位毫秒
    private final int EVENT_DURATION = 10;
    // 生成的事件个数
    private final int EVENT_COUNT = 20;
    private CountDownLatch countDownLatch;
    private MyEventSource eventSource;
    private Flux<MyEvent> fastPublisher;
    private SlowSubscriber slowSubscriber;

    /**
     * 生成一个发出元素较快的发布者
     *
     * @param strategy  回压策略
     * @return          发布者
     */
    private Flux<MyEvent> createFlux(FluxSink.OverflowStrategy strategy) {
        return Flux.create(sink -> eventSource.register(new MyEventListener() {
            @Override
            public void onNewEvent(MyEvent event) {
                System.out.println("publishi >>> " + event.getMessage());
                sink.next(event);
            }

            @Override
            public void onEventStopped() {
                sink.complete();
            }
        }), strategy);
    }

    /**
     * 生成事件
     *
     * @param times     生成多少个
     * @param millis    生成事件的间隔
     */
    private void generateEvent(int times, int millis) {
        for (int i = 0; i < times; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            eventSource.newEvent(new MyEvent(new Date(), "Event-" + i));
        }
        eventSource.eventStopped();
    }

    /**
     * 准备工作
     */
    @Before
    public void setup() {
        countDownLatch = new CountDownLatch(1);
        // 定义一个处理元素较慢的订阅者
        slowSubscriber = new SlowSubscriber(countDownLatch);
        eventSource = new MyEventSource();
    }

    /**
     * 触发订阅
     *
     * @throws InterruptedException 线程中断
     */
    @After
    public void subscribe() throws InterruptedException {
        // 订阅方法
        fastPublisher.subscribe(slowSubscriber);
        // 生成事件
        generateEvent(EVENT_COUNT, EVENT_DURATION);
        // 等待任务结束
        countDownLatch.await(1, TimeUnit.MINUTES);
    }

    @Test
    public void testCreateBackPress() {
        // 订阅处理流
        // 可以指定不同的回压策略，默认为BUFFER
        fastPublisher = createFlux(FluxSink.OverflowStrategy.IGNORE)
                // 也可以通过onBackpressurexxx()调整回压策略
                .onBackpressureLatest()
                .doOnRequest(n -> System.out.println("  === request:" + n + "==="))
                // 通常发布者和订阅者不在同一线程，这里使用publishOn模拟
                // 第二个参数表示向上游预先取多少个元素，一定程度上起到了缓存作用，默认256，可以更改下查看结果
                .publishOn(Schedulers.newSingle("newsingle"), 1);
    }

}
