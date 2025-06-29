package com.example.practice_sse.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class DebugSSEController {

    @GetMapping(value = "/api/debug-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter debugEvents() {
        // 콘솔에 연결 시작 로그
        System.out.println("=== 새로운 SSE 연결 요청 받음 ===");
        System.out.println("요청 시간: " + LocalDateTime.now());

        // 30초 타임아웃 설정
        SseEmitter emitter = new SseEmitter(30000L);

        // 연결 상태별 콜백 등록

        // onCompletion: 정상적으로 모든 데이터 전송이 끝나고 연결이 종료됐을 때
        emitter.onCompletion(() -> {
            System.out.println("✅ SSE 연결 정상 종료");
            System.out.println("종료 시간: " + LocalDateTime.now());
        });

        // onTimeout: 지정된 타임아웃 시간이 지나서 연결이 종료됐을 때
        emitter.onTimeout(() -> {
            System.out.println("⏱️ SSE 연결 타임아웃 (30초)");
            System.out.println("타임아웃 시간: " + LocalDateTime.now());
        });

        // onError: 전송 중 에러가 발생했을 때
        emitter.onError((ex) -> {
            System.out.println("❌ SSE 연결 에러: " + ex.getMessage());
            System.out.println("에러 발생 시간: " + LocalDateTime.now());
            ex.printStackTrace(); // 상세 에러 스택 출력
        });

        // 별도 스레드에서 메시지 전송
        new Thread(() -> {
            try {
                // 5개의 메시지를 2초 간격으로 전송
                for (int i = 1; i <= 5; i++) {
                    System.out.println("📤 메시지 " + i + " 전송 준비");

                    // 더 상세한 메시지 생성
                    String message = String.format(
                            "메시지 %d (전송시간: %s)",
                            i,
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    );

                    // 메시지 전송
                    emitter.send(SseEmitter.event()
                            .name("debug-message")
                            .id("debug-" + i)
                            .data(message));

                    System.out.println("✅ 메시지 " + i + " 전송 완료");

                    Thread.sleep(2000); // 2초 대기
                }

                // 마지막 메시지
                emitter.send(SseEmitter.event()
                        .name("completion")
                        .data("모든 디버그 메시지 전송 완료"));

                System.out.println("🏁 모든 메시지 전송 완료, 연결 종료 시작");
                emitter.complete();

            } catch (InterruptedException e) {
                System.out.println("🔄 스레드 인터럽트 발생");
                Thread.currentThread().interrupt();
                emitter.completeWithError(e);
            } catch (Exception e) {
                System.out.println("💥 예상치 못한 에러 발생: " + e.getMessage());
                emitter.completeWithError(e);
            }
        }).start();

        System.out.println("🚀 SSE Emitter 반환, 비동기 전송 시작");
        return emitter;
    }
}