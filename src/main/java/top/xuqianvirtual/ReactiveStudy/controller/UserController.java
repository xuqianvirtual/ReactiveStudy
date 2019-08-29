package top.xuqianvirtual.ReactiveStudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.xuqianvirtual.ReactiveStudy.model.User;
import top.xuqianvirtual.ReactiveStudy.service.UserService;

import java.time.Duration;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("")
    public Mono<User> save(User user)  {
        return userService.save(user);
    }

    @DeleteMapping("/{username}")
    public Mono<Long> deleteByUsername(@PathVariable String username) {
        return userService.deleteByUsername(username);
    }

    @GetMapping("/{username}")
    public Mono<User> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @GetMapping("")
    public Flux<User> findAll() {
        return userService.findAll();
    }

    // 异步响应式获取所有用户
    @GetMapping(value = "/getAllAsync", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<User> findAllAsync() {
        return userService.findAll().delayElements(Duration.ofSeconds(2));
    }
}
