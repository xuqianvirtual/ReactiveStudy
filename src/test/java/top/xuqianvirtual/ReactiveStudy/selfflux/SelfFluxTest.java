package top.xuqianvirtual.ReactiveStudy.selfflux;

import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import top.xuqianvirtual.ReactiveStudy.selfflux.tools.Flux;

public class SelfFluxTest {

    @Test
    public void testFluxArray() {
        // 使用自定义的响应式流进行测试
        // 实现一个订阅者
        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                subscription.request(6);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext:" + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete!");
            }
        };

        // 产生流并订阅
        Flux.just(1,2,3,4,5).subscribe(subscriber);

        // 使用map加一后再输出
        Flux.just(1,2,3,4,5).map(i -> i + 1).subscribe(subscriber);
    }
}
