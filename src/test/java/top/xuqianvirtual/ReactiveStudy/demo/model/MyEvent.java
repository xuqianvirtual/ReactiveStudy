package top.xuqianvirtual.ReactiveStudy.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyEvent {
    private Date timestamp;
    private String message;
}