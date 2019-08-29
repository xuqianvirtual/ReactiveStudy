package top.xuqianvirtual.ReactiveStudy.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;
import top.xuqianvirtual.ReactiveStudy.model.Event;

public interface EventRepository extends ReactiveMongoRepository<Event, Long> {

    // 类似tail -f，发送完之后不会结束，没有完成事件，有新元素将会继续发送
    // 注意：返回Flux的方法才能使用该注解
    // 注意：仅支持有大小限制的collection，具体如何配置查看MongoDBConfig.initData方法
    @Tailable
    Flux<Event> findBy();
}
