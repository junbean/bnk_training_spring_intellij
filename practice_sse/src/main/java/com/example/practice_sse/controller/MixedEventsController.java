package com.example.practice_sse.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class MixedEventsController {

    @GetMapping(value = "/api/mixed-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter mixedEvents() {
        // 기본 타임아웃 사용 (30초)
        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                // 1. 환영 메시지 (welcome 이벤트)
                emitter.send(SseEmitter.event()
                        .name("welcome")           // event: welcome
                        .data("SSE 연결 성공!")); // data: SSE 연결 성공!

                Thread.sleep(2000); // 2초 대기

                // 2. 알림 메시지 (notification 이벤트)
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data("새로운 알림이 있습니다"));

                Thread.sleep(2000);

                // 3. JSON 데이터 (data 이벤트)
                // JSON 형태의 문자열 전송
                String jsonData = "{\"user\": \"김철수\", \"status\": \"온라인\", \"loginTime\": \"" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\"}";

                emitter.send(SseEmitter.event()
                        .name("data")
                        .data(jsonData));

                Thread.sleep(2000);

                // 4. ID가 있는 메시지 (순서 보장용)
                emitter.send(SseEmitter.event()
                        .name("numbered")
                        .id("msg-001")  // id: msg-001
                        .data("첫 번째 번호가 있는 메시지"));

                Thread.sleep(1000);

                emitter.send(SseEmitter.event()
                        .name("numbered")
                        .id("msg-002")
                        .data("두 번째 번호가 있는 메시지"));

                Thread.sleep(2000);

                // 5. 마지막 메시지
                emitter.send(SseEmitter.event()
                        .name("goodbye")
                        .data("모든 메시지 전송 완료. 연결을 종료합니다."));

                // 모든 전송 완료
                emitter.complete();

            } catch (Exception e) {
                System.err.println("SSE 전송 중 오류: " + e.getMessage());
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}