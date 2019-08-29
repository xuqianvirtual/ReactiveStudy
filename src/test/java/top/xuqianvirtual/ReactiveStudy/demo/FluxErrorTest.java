package top.xuqianvirtual.ReactiveStudy.demo;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;

/**
 * 四、错误处理
 */
public class FluxErrorTest {

    @Test
    public void testErrorSignal() {
        // 错误会导致流序列停止，并向后传递直到遇到subscribe中的错误处理方法
        // 注意：如果不处理，错误会抛到更上层，建议在subscribe中国处理
        Flux.range(1, 6).map(i -> 10 / (i - 3)).map(i -> i*i)
                .subscribe(
                        System.out::println,
                        // 错误处理使得程序能够正常退出，若无则异常退出
                        System.err::println
                );
    }

    @Test
    public void testErrorHandling() {
        // 常见的错误处理方式
        // 1.捕获并返回一个静态的缺省值
        Flux.range(1, 6).map(i -> 10 / (i - 3))
                .onErrorReturn(0)   // 虽然程序依然终止了，但是并没有输出异常
                .map(i -> i*i).subscribe(System.out::println, System.err::println);
        System.out.println();

        // 2.捕获并执行一个异常处理方法或动态计算一个候补值替代
        Flux.range(1, 6).map(i -> 10 / (i - 3))
                // 虽然程序依然终止了，但是并没有输出异常，并生成了一个小于6的随机值继续运算下去
                .onErrorResume(e -> Mono.just(new Random().nextInt(6)))
                .map(i -> i*i).subscribe(System.out::println, System.err::println);
        System.out.println();

        // 3.捕获并转换异常
        Flux.range(1, 6).map(i -> 10 / (i - 3))
                // 捕获异常后，可以转换成其他异常抛出
                .onErrorMap(e -> new RuntimeException("业务异常"))
                .map(i -> i*i).subscribe(System.out::println, System.err::println);

        Flux.range(1, 6).map(i -> 10 / (i - 3))
                // 也可以用onErrorResume实现，生成一个只带错误信号的流即可
                .onErrorResume(e -> Flux.error(new RuntimeException("业务异常")))
                .map(i -> i*i).subscribe(System.out::println, System.err::println);
        System.out.println();

        // 4.捕获，处理，然后继续抛出
        Flux.range(1, 6).map(i -> 10 / (i - 3))
                // doOn方法都不会改动流中的数据，所以这里打印堆栈后，还会再subscribe中再打印一次
                .doOnError(Throwable::printStackTrace)
                .map(i -> i*i).subscribe(System.out::println, System.err::println);
        System.out.println();

        // 5.使用类似java异常处理的形式关闭资源（finally或try-with-resource）
        // 伪代码，未具体实现，类似try-with-resource形式
//        Flux.using(
//                () -> getResource(),  // 获取资源
//                resource -> Flux.just(resource.getAll()),    // 从资源生成数据流
//                MyResource::clean        // 清理资源
//        );
        //
        Flux.just("foo", "bar", "2000")
                // doFinally表示在序列结束后所作的操作，能够获取到序列结束时发出的信号，利用此功能可以做资源清理等操作
                // take会以取消的方式结束，所以之前的doFinally获取到cancel信号
                .doFinally(System.out::println).take(1)
                // take会以完成的方式结束，所以之前的doFinally获取到onComplete信号
                .doFinally(System.out::println).subscribe(System.out::println, System.err::println);
        System.out.println();
    }

}
