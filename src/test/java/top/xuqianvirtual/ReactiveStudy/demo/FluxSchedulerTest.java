package top.xuqianvirtual.ReactiveStudy.demo;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 三、调度器与线程模型
 */
public class FluxSchedulerTest {

    /**
     * 当前线程：Schedulers.immediate()
     * 可重用的单线程：Schedulers.single()。
     * 独占的单线程：Schedulers.newSingle()
     * 弹性线程池:Schedulers.elastic()。默认空闲60s则回收
     * 固定大小线程池:Schedulers.parallel()。数量等同于CPU核心数
     * 自定义线程池:Schedulers.fromExecutorService(ExecutorService)
     * 除当前线程和自定义线程池外，其他方法加上new均可以创建新的线程池
     */

    private String getStringSync() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello, Reactor！";
    }

    @Test
    public void testScheduler() throws InterruptedException {
        // 将同步的阻塞调用变为异步
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Mono.fromCallable(this::getStringSync)
                // 使用fromCallable声明一个基于Callable的Mono
                .subscribeOn(Schedulers.elastic())
                // 使用subscribeOn将任务调度到Schedulers内置的弹性线程池执行，
                // 弹性线程池会为Callable的执行任务分配一个单独的线程。
                .subscribe(System.out::println, null, countDownLatch::countDown);


        // 切换调度器的操作符
        Flux.range(1, 200)
                .map(i -> new Integer[]{i, i % 2})
                // publishOn影响其后的操作符，比如filter使用的是Schedulers.elastic()线程池
                .publishOn(Schedulers.elastic()).filter(i -> i[1] == 0)
                // flatMap使用的是Schedulers.parallel()线程池
                .publishOn(Schedulers.parallel()).flatMap(Flux::fromArray)
                // subscribeOn影响源及之后使用的调度器，直到被publishOn切换
                .subscribeOn(Schedulers.single())
                .subscribe(System.out::println, null, countDownLatch::countDown);

        countDownLatch.await(20, TimeUnit.SECONDS);
    }
}
