package com.example.practice_sse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/simpleSseIndex")
    public String simpleSseIndex() {
        return "simpleSseIndex";
    }

    @GetMapping("/timeSseIndex")
    public String timeSseIndex() {
        return "timeSseIndex";
    }

    @GetMapping("/mixedEventsIndex")
    public String mixedEventsIndex() {
        return "mixedEventsIndex";
    }

    @GetMapping("/debugSseIndex")
    public String debugSseIndex() {
        return "debugSseIndex";
    }
}
