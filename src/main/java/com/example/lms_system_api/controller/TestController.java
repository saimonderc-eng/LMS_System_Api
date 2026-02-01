package com.example.lms_system_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/admin/welcome")
    public String adminHello(){
        return "Welcome, ADMIN! you have access to this area";
    }
    @GetMapping("/teacher/welcome")
    public String teacherHello(){
        return "Welcome, TEACHER! you have access to this area";
    }
    @GetMapping("/public/welcome")
    public String publicHello(){
        return "Welcome, this area dont need token!";
    }
}
