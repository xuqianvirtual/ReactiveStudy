package top.xuqianvirtual.ReactiveStudy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "event") // 指定collection为event
public class Event {

    private Long id;
    
    private String message;
}
