package top.xuqianvirtual.ReactiveStudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.xuqianvirtual.ReactiveStudy.model.Event;
import top.xuqianvirtual.ReactiveStudy.repository.EventRepository;

@RestController
@RequestMapping("/events")
public class EventController {
    @Autowired
    private EventRepository eventRepository;

    @PostMapping(path = "", consumes = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<Void> loadEvents(@RequestBody Flux<Event> events) {
        // insert返回保存的元素，不需要的话直接调用then，可以返回完成信号
        return eventRepository.insert(events).then();
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Event> getEvents() {
        return eventRepository.findBy();
    }
}
