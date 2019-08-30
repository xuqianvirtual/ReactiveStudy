package top.xuqianvirtual.ReactiveStudy.selfflux.tools;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.function.Function;

// 定义Flux，响应式流的工具类，同时也是发布者
// 继承Publisher，即发布者
public abstract class Flux<T> implements Publisher<T> {
    public abstract void subscribe(Subscriber<? super T> subscriber);

    // 实现just方法，用于生成流
    public static <T> Flux<T> just(T...data) {
        return new FluxArray<>(data);
    }

    // 定义map方法，用于转换流
    public <V> Flux<V> map(Function<?super T, ? extends V> mapper) {
        return new FluxMap<>(this, mapper);
    }
}
