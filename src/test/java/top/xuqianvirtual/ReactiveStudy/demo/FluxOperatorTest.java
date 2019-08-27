package top.xuqianvirtual.ReactiveStudy.demo;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 二、操作符
 * 完整的操作符查看：
 * https://htmlpreview.github.io/?https://github.com/get-set/reactor-core/blob/master-zh/src/docs/index.html
 */
public class FluxOperatorTest {

    @Test
    public void operatorTest() {
        // map - 元素映射为新元素
        StepVerifier.create(Flux.range(1, 6).map(i -> i * i))
                .expectNext(1, 4, 9, 16, 25, 36)
                .verifyComplete();

        // flatMap - 元素映射为流
        StepVerifier.create(
                Flux.just("flux", "mono").flatMap(
                        s -> Flux.fromArray(
                                // 字符串拆分为单个字符并打平成一个流
                                s.split("\\s*"))
                                // 每个元素延迟100ms
                                .delayElements(Duration.ofMillis(100)))
                        // 打印每个元素，但不会消费掉
                        .doOnNext(System.out::print))
                // 期望发出8个元素
                .expectNextCount(8).verifyComplete();

        // filter - 过滤
        StepVerifier.create(Flux.range(1, 6)
                .filter(i -> i % 2 == 1).map(i -> i * i))
                .expectNext(1, 9, 25)
                .verifyComplete();
    }

    private Flux<String> getZipDescFlux() {
        String desc = "Zip two sources together, that is to say wait for all the sources to emit one element and combine these elements once into a Tuple2.";
        return Flux.fromArray(desc.split("\\s+"));  // 1
    }

    @Test
    public void testZipFlux() throws InterruptedException {
        // zip - 一对一合并
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux.zip(getZipDescFlux(),
                // 从0开始递增，没200ms产生一个数字
                Flux.interval(Duration.ofMillis(200)))
                // 第一个流与第二个流一对一合并，所以第一个流就拥有了第二个流的速度
                .subscribe(t -> System.out.println(t.getT1()),
                        // 结束时执行countDown，否则程序不会等待200ms的频率
                        null, countDownLatch::countDown);
        countDownLatch.await(20, TimeUnit.SECONDS);
    }
}
