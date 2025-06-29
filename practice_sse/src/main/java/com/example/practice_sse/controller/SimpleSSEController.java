package com.example.practice_sse.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;

@RestController
public class SimpleSSEController {

    // @GetMapping: HTTP GET 요청을 처리하는 메서드임을 지정
    // value = "/api/simple-events": 이 URL로 요청이 오면 이 메서드가 실행됨
    // produces = MediaType.TEXT_EVENT_STREAM_VALUE: 응답 타입을 "text/event-stream"으로 설정
    //   → 이것이 바로 SSE의 핵심! 브라우저에게 "이건 SSE 스트림이야"라고 알려줌
    @GetMapping(value = "/api/simple-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter simpleEvents() {

        // SseEmitter: Spring Boot에서 SSE를 구현하기 위한 핵심 클래스
        // 이 객체를 통해 클라이언트에게 데이터를 전송할 수 있음
        SseEmitter emitter = new SseEmitter();

        // 왜 별도 스레드를 사용하나?
        // → 메인 스레드에서 sleep()하면 다른 요청들이 블록됨
        // → 비동기 처리를 위해 새로운 스레드에서 데이터 전송
        new Thread(() -> {
            try {
                // 1부터 10까지 카운터 전송
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(1000); // 1초(1000ms) 대기

                    // emitter.send(): 클라이언트에게 데이터 전송
                    // 내부적으로 "data: 카운터: 1\n\n" 형태로 변환되어 전송됨
                    emitter.send("카운터: " + i);
                }

                // emitter.complete(): 모든 데이터 전송이 끝났음을 알리고 연결 종료
                // 클라이언트에서 onmessage가 아닌 onerror 이벤트가 발생함
                emitter.complete();

            } catch (Exception e) {
                // 에러 발생 시 클라이언트에게 에러를 알리고 연결 종료
                emitter.completeWithError(e);
            }
        }).start(); // 스레드 시작

        // emitter 객체를 즉시 반환
        // 실제 데이터는 별도 스레드에서 비동기로 전송됨
        return emitter;
    }

}
