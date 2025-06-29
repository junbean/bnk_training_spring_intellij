package com.example.practice_sse.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ExController {

    @GetMapping(value = "/sse1", produces = "text/event-stream;charset=UTF-8")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    emitter.send("메시지 " + i + "번째");
                    Thread.sleep(1000); // 1초 간격
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    @GetMapping(value = "/sse2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream2() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 타임아웃 설정

        // 별도 스레드에서 데이터 전송
        CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000); // 1초 대기
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data("메시지 " + i));
                }
                emitter.complete(); // 연결 종료
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
