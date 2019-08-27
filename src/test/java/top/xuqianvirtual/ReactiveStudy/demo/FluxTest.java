package top.xuqianvirtual.ReactiveStudy.demo;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 一、Flux上手
 */
public class FluxTest {

    @Test
    public void fluxAndMono() {
        // 直接生成数据流
        Flux.just(1, 2, 3, 4, 5, 6);
        Mono.just(1);

        // 基于数组、集合、Stream生成
        Integer[] array = new Integer[]{1, 2, 3, 4, 5, 6};
        Flux.fromArray(array);
        List<Integer> list = Arrays.asList(array);
        Flux.fromIterable(list);
        Stream<Integer> stream = list.stream();
        Flux.fromStream(stream);

        // 只有完成信号的空数据流
        Flux.just();
        Flux.empty();
        Mono.empty();
        Mono.justOrEmpty(Optional.empty());

        // 只有错误信号的数据流
        Flux.error(new Exception("some error"));
        Mono.error(new Exception("some error"));

        // 订阅前什么都不会发生
        Flux.just(1, 2, 3, 4, 5, 6).subscribe(System.out::print);
        System.out.println();
        Mono.just(1).subscribe(System.out::print);
        System.out.println();

        // 订阅并处理数据流
        Flux.just(1, 2, 3, 4, 5, 6).subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("Compeleted!"));

        // 错误信号例子
        Mono.error(new Exception("some error")).subscribe(
                System.out::println,
                System.err::println,
                () -> System.out.println("Compeleted!"));
    }

    private Flux<Integer> generateFluxFrom1To6() {
        return Flux.just(1, 2, 3, 4, 5, 6);
    }

    private Mono<Integer> generateMonoWithError() {
        return Mono.error(new Exception("some error"));
    }

    @Test
    public void testViaStepVerifier() {
        // 使用StepVerifier对Flux进行测试
        StepVerifier.create(generateFluxFrom1To6())
                .expectNext(1, 2, 3, 4, 5, 6)
                .expectComplete()
                .verify();
        StepVerifier.create(generateMonoWithError())
                .expectErrorMessage("some error")
                .verify();
    }
}
