package com.example.lms_system_api.controller;

import com.example.lms_system_api.dto.UpdatePasswordRequest;
import com.example.lms_system_api.dto.UpdateUserRequest;
import com.example.lms_system_api.dto.UserDto;
import com.example.lms_system_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> homePage(){
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request){
        userService.update(request);
        return ResponseEntity.ok("User details updated!");
    }
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordRequest request){
        userService.updatePassword(request);
        return ResponseEntity.ok("Password successfully updated!");
    }
}
