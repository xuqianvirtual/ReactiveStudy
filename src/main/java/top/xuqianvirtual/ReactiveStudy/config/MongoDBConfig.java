package top.xuqianvirtual.ReactiveStudy.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import top.xuqianvirtual.ReactiveStudy.model.Event;

@Configuration
public class MongoDBConfig {

    @Bean
    public CommandLineRunner initData(MongoOperations mongo) {
        return (String...args) -> {
            // 项目启动时链接MongoDB进行的操作
            // 删除类对应的collection，线上肯定不能用
            mongo.dropCollection(Event.class);
            // 创建有限制的collection
            mongo.createCollection(Event.class, CollectionOptions.empty()
                    .maxDocuments(100)  //限制最大记录数
                    .size(100000)   // 限制容量
                    .capped());
        };
    }
}
