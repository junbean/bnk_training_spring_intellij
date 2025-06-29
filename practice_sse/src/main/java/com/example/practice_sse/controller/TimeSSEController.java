package com.example.practice_sse.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class TimeSSEController {

    @GetMapping(value = "/api/current-time", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter currentTime() {

        // SseEmitter(60000L): 60초(60,000ms) 타임아웃 설정
        // 60초 동안 데이터 전송이 없으면 자동으로 연결 종료
        // 기본값은 30초
        SseEmitter emitter = new SseEmitter(60000L);

        // ScheduledExecutorService: 정해진 시간마다 작업을 반복 실행
        // newSingleThreadScheduledExecutor(): 단일 스레드로 스케줄링
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        // scheduleAtFixedRate(): 고정 간격으로 작업 실행
        // 매개변수: (실행할 작업, 최초 지연시간, 반복 간격, 시간 단위)
        executor.scheduleAtFixedRate(() -> {
            try {
                // 현재 시간을 "yyyy-MM-dd HH:mm:ss" 형태로 포맷
                String currentTime = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // SseEmitter.event(): 더 상세한 이벤트 생성
                // .name(): 이벤트 타입 지정 (클라이언트에서 addEventListener로 처리 가능)
                // .data(): 실제 전송할 데이터
                emitter.send(SseEmitter.event()
                        .name("time-update")  // event: time-update
                        .data(currentTime));  // data: 2024-06-26 14:30:15

            } catch (Exception e) {
                // 전송 중 에러 발생 시 연결 종료 및 스케줄러 정리
                emitter.completeWithError(e);
                executor.shutdown(); // 중요: 메모리 누수 방지
            }
        }, 0, 1, TimeUnit.SECONDS); // 즉시 시작, 1초마다 반복

        // 연결 정상 종료 시 스케줄러 정리
        emitter.onCompletion(() -> {
            System.out.println("시간 SSE 연결 정상 종료");
            executor.shutdown();
        });

        // 타임아웃 시 스케줄러 정리
        emitter.onTimeout(() -> {
            System.out.println("시간 SSE 연결 타임아웃");
            executor.shutdown();
        });

        return emitter;
    }
}