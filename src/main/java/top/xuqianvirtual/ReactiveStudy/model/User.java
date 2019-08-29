package top.xuqianvirtual.ReactiveStudy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data   // 生成无参构造方法/getter/setter/hashCode/equals/toString
@AllArgsConstructor // 生成给所有参数构造方法
@NoArgsConstructor  // @AllArgsConstructor会导致@Data不生成无参构造方法，需要手动添加
@Document   // MongoDB是文档型数据库，所有使用@Document注解
public class User {
    @Id //表示id为主键
    private String id;
    @Indexed(unique = true) // 表示username为索引，且无重复
    private String username;
    private String phone;
    private String email;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
}
