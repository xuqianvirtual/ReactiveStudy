package top.xuqianvirtual.ReactiveStudy.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

@Component
public class TimeHandler {
    // 返回当前时间
    // 与mvc不同，请求与返回不再是ServletRequest和ServletResponse，而是ServerRequest和ServerResponse
    public Mono<ServerResponse> getTime(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("Now is " +
                        new SimpleDateFormat("HH:mm:ss").format(new Date())),
                        String.class);
    }

    // 获取当前日期
    public Mono<ServerResponse> getDate(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(Mono.just("Today is " +
                                new SimpleDateFormat("yyyy-MM-dd").format(new Date())),
                        String.class);
    }

    // 每秒向客户端推送一次时间
    public Mono<ServerResponse> sendTimePerSec(ServerRequest serverRequest) {
        // MediaType.TEXT_EVENT_STREAM表示Content-Type为text/evnet-stream，即SSE(Server Send Event)
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(
                Flux.interval(Duration.ofSeconds(1))
                        .map(l -> new SimpleDateFormat("HH:mm:ss").format(new Date())),
        String.class);
    }
}
