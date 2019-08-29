package top.xuqianvirtual.ReactiveStudy.webclient;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import top.xuqianvirtual.ReactiveStudy.model.User;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WebClientTest {

    @Test
    public void testWebClient() throws InterruptedException {
        // 请求hello
        CountDownLatch countDownLatch = new CountDownLatch(1);
        WebClient webClient = WebClient.create("http://localhost:8080");
        webClient.get().uri("/hello")
                .retrieve()     // 表示异步获取response body信息
                .bodyToMono(String.class)   // 将response body解析为字符串
                // 由于请求是异步的，使用CountDownLatch保证执行完毕
                .subscribe(System.out::println, null, countDownLatch::countDown);
        countDownLatch.await(110, TimeUnit.SECONDS);
    }

    @Test
    public void testWebClientAsync() throws InterruptedException {
        // 异步获取所有用户
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();
        webClient.get().uri("/user/getAllAsync")
                .accept(MediaType.APPLICATION_STREAM_JSON)  // 表示接收的是stream+json
                .exchange() // 返回的是response信息，返回的信息要比retrieve多，但之后的处理也要复杂一些
                // flatMapMany是指在Mono元素异步返回多个序列时使用的，区别于flatMap
                .flatMapMany(response -> response.bodyToFlux(User.class)) // 将ClientResponse映射为Flux
                .doOnNext(System.out::println)  // 窃取并打印每个元素
                .blockLast();   // 阻塞直到获取到最后一个元素，线上慎用
    }

    @Test
    public void testTimes()  {
        // 测试时间推送
        WebClient webClient = WebClient.create("http://localhost:8080");
        webClient.get().uri("/times")
                .accept(MediaType.TEXT_EVENT_STREAM)    // 服务器推送事件，SSE
                .retrieve()
                .bodyToFlux(String.class)
                .log()  // 等价于doOnNext(System.out::println)
                .take(10)   // 截取10个
                .blockLast();
    }
}
