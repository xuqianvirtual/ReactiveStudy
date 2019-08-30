package top.xuqianvirtual.ReactiveStudy.selfflux.tools;

import org.reactivestreams.Subscriber;

import java.util.function.Function;

public class FluxMap<T, R> extends Flux<R> {
    private final Flux<? extends T> source;
    private final Function<? super T, ? extends R> mapper;

    public FluxMap(Flux<? extends T> source, Function<? super T, ? extends R> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public void subscribe(Subscriber<? super R> subscriber) {
        source.subscribe(new MapSubscriber<>(subscriber, mapper));
    }
}
