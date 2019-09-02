package top.xuqianvirtual.ReactiveStudy.demo;

import org.junit.Test;
import reactor.core.publisher.Flux;
import top.xuqianvirtual.ReactiveStudy.demo.model.MyEvent;
import top.xuqianvirtual.ReactiveStudy.demo.model.MyEventListener;
import top.xuqianvirtual.ReactiveStudy.demo.model.MyEventSource;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 六、自定义数据流
 */
public class CustomizeDataFluxTest {

    // generate是同步的、逐个发送数据的方法，
    // 其sink是一个SynchronousSink，回调方法next()每次最多只能被调用一次
    @Test
    public void testGenerate() {
        final AtomicInteger count = new AtomicInteger(1);
        Flux.generate(sink -> {
            // 向数据池放入自定义的数据
            sink.next(count.get() + ":" + new Date());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (count.getAndIncrement() >= 5)
                // 告知已经发完数据了
                sink.complete();
        }).subscribe(System.out::println);

        // 使用伴随状态
        Flux.generate(() -> 1, (number, sink) -> {
            sink.next(number + ":" + new Date());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (number >= 5) sink.complete();
            return number + 1;
        }).subscribe(System.out::println);

        // 执行结束后打印伴随状态，可用于执行资源清理
        Flux.generate(() -> 1, (number, sink) -> {
            sink.next(number + ":" + new Date());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (number >= 5) sink.complete();
            return number + 1;
        }, System.out::println).subscribe(System.out::println);
    }

    // create()方法比generate()更高级，生成数据流的方式可以同步也可以异步，每次可以发出多个元素
    @Test
    public void testCreate() throws InterruptedException {
        // 定义事件源
        MyEventSource source = new MyEventSource();
        // create替换为generate后会报异常，因为generate不支持异步
        // 可替换为push，区别在于push中调用next、complete或error必须是同一线程
        Flux.create(sink -> {
            // 注册监听器
            source.register(new MyEventListener() {
                @Override
                public void onNewEvent(MyEvent event) {
                    // 收到回调后再将事件发出
                    sink.next(event);
                }

                @Override
                public void onEventStopped() {
                    // 收到事件源停止后发出完成信号
                    sink.complete();
                }
            });
        }).subscribe(System.out::println);

        for (int i = 0; i< 20; i++) {
            Random random = new Random();
            TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            source.newEvent(new MyEvent(new Date(), "Event-" + i));
        }
        source.eventStopped();
    }
}
