package top.xuqianvirtual.ReactiveStudy.demo.model;

import java.util.LinkedList;
import java.util.List;

public class MyEventSource {
    private List<MyEventListener> listenners;
    public MyEventSource() {
        this.listenners = new LinkedList<>();
    }
    public void register(MyEventListener listener) {
        // 注册监听器
        this.listenners.add(listener);
    }
    public void newEvent(MyEvent event) {
        // 发送新事件
        for (MyEventListener listener:listenners)
            listener.onNewEvent(event);
    }
    public void eventStopped() {
        // 告知监听器事件源已停止
        for (MyEventListener listener:listenners)
            listener.onEventStopped();
    }
}
