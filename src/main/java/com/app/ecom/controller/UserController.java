package com.app.ecom.controller;

import com.app.ecom.dto.*;
import com.app.ecom.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<UserCreateResponseDto>> createUser(@RequestBody UserCreateRequestDto request) {
        return userService.createUser(request);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequestDto request) {
        return userService.updateUser(id, request);
    }
}
