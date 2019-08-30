package top.xuqianvirtual.ReactiveStudy.selfflux.tools;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Function;

public class MapSubscriber<T, R> implements Subscriber<T>, Subscription {

    private final Subscriber<? super R> actual;
    private final Function<? super T, ? extends R> mapper;
    private boolean done;
    private Subscription subscription;

    MapSubscriber(Subscriber<? super R> actual, Function<? super T, ? extends R> mapper) {
        this.actual = actual;
        this.mapper = mapper;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        actual.onSubscribe(this);
    }

    @Override
    public void onNext(T t) {
        if (done) return;
        actual.onNext(mapper.apply(t));
    }

    @Override
    public void onError(Throwable throwable) {
        if (done) return;
        done =  true;
        actual.onError(throwable);
    }

    @Override
    public void onComplete() {
        if (done) return;
        done = true;
        actual.onComplete();
    }

    @Override
    public void request(long l) {
        this.subscription.request(l);
    }

    @Override
    public void cancel() {
        this.subscription.cancel();
    }
}
