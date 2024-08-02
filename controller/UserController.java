package com.app.user.controller;

import com.app.user.dto.UserGetInfoRequest;
import com.app.user.dto.UserRegisterRequest;
import com.app.user.dto.UserUpdateRequest;
import com.app.user.dto.ApiResponse;
import com.app.user.model.User;
import com.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse register(@RequestBody UserRegisterRequest request) {
        try {
            User user = userService.registerUser(request.getUsername(), request.getPassword());
            return new ApiResponse(200, "Registration successful", user);
        } catch (Exception e) {
            return new ApiResponse(403, e.getMessage(), null);
        }
    }

    @PostMapping("/update")
    public ApiResponse update(@RequestBody UserUpdateRequest request) {
        try {
            // Assume userId is passed as a parameter or in the request context
            Long userId = 1L;  // Replace with actual logic to get userId
            User user = userService.updateUser(userId, request);
            return new ApiResponse(200, "Update successful", user);
        } catch (Exception e) {
            return new ApiResponse(403, e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody UserRegisterRequest request) {
        try {
            User user = userService.loginUser(request.getUsername(), request.getPassword());
            return new ApiResponse(200, "Login successful", user);
        } catch (Exception e) {
            return new ApiResponse(403, e.getMessage(), null);
        }
    }

    @GetMapping("/info/detail/{userId}")
    public ApiResponse getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserInfo(userId);
            return new ApiResponse(200, "User found", user);
        } catch (Exception e) {
            return new ApiResponse(403, e.getMessage(), null);
        }
    }
    // Other methods...
}
