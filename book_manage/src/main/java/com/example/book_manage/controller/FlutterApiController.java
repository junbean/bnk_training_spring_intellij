package com.example.book_manage.controller;

import com.example.book_manage.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/basic")
public class FlutterApiController {

    @GetMapping("/user")
    public ResponseEntity<UserResponse> getUser() {
        UserResponse response = new UserResponse();
        response.setName("James Dean");
        response.setAge(46);
        response.setMessage("Hello~");

        return ResponseEntity.ok().body(response);
    }
}
