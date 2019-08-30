package top.xuqianvirtual.ReactiveStudy.selfflux.tools;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

// 继承Flux
public class FluxArray<T> extends Flux<T> {
    private T[] array;

    public FluxArray(T[] data) {
        this.array = data;
    }

    // 实现subscribe方法，即订阅，由外部将订阅者传入
    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new ArraySubscription<>(subscriber, array));
    }

    // 实现该发布者对应的subcription，用于定义如何发布者如何与订阅者交互
    static class ArraySubscription<T> implements Subscription {
        final Subscriber<? super T> actual;
        final T[] array;
        int index;
        boolean canceled;

        public ArraySubscription(Subscriber<? super T> actual, T[] array) {
            this.actual = actual;
            this.array = array;
        }

        // 当订阅者请求l个元素时，执行的动作
        @Override
        public void request(long l) {
            if (canceled) return;
            long length = array.length;
            for (int i = 0; i < l && index < length; i++) {
                actual.onNext(array[index++]);
            }
            if (index == length) actual.onComplete();
        }

        // 当订阅者取消订阅时，执行的动作
        @Override
        public void cancel() {
            this.canceled = true;
        }
    }
}
