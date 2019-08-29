package top.xuqianvirtual.ReactiveStudy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.xuqianvirtual.ReactiveStudy.model.User;
import top.xuqianvirtual.ReactiveStudy.repository.UserRepository;

@Service
public class UserService {

    // 目前Spring Data支持的可以进行响应式数据访问的数据库有
    // MongoDB、Redis、Apache Cassandra和CouchDB
    @Autowired
    private UserRepository userRepository;

    public Mono<User> save(User user) {
        return userRepository.save(user)
                .onErrorResume(e ->
                        userRepository.findByUsername(user.getUsername()))
                .flatMap(originalUser  -> {
                    user.setId(originalUser.getId());
                    return userRepository.save(user);
                });
    }

    public Mono<Long> deleteByUsername(String username) {
        return userRepository.deleteByUsername(username);
    }

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }
}
