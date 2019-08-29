package top.xuqianvirtual.ReactiveStudy.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import top.xuqianvirtual.ReactiveStudy.model.User;

public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Mono<User> findByUsername(String username);
    Mono<Long> deleteByUsername(String username);
}
