package top.xuqianvirtual.ReactiveStudy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

// RestController和GetMapping注解是mvc形式的，WebFlux对齐进行了支持
// 但是新服务最好还是使用函数式风格的代码
@RestController
public class HelloController {

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Welcom to reactive world ~");
    }
}
