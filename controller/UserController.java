package com.app.user.controller;

import com.app.user.vo.UserVO;
import com.app.user.dto.UserGetInfoRequest;
import com.app.user.dto.UserRegisterRequest;
import com.app.user.dto.UserUpdateRequest;
import com.app.user.dto.ApiResponse;
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
            UserVO userVO = userService.registerUser(request.getUsername(), request.getPassword());
            return new ApiResponse(200, "Registration successful", userVO);
        } catch (Exception e) {
            return new ApiResponse(403, e.getMessage(), null);
        }
    }

    @PostMapping("/update")
    public ApiResponse update(@RequestBody UserUpdateRequest request) {
        try {
            // 假设 userId 作为参数传递或从上下文中获取
            Long userId = 1L;  // 使用实际逻辑获取 userId
            UserVO userVO = userService.updateUser(userId, request);
            return new ApiResponse(200, "Update successful", userVO);
        } catch (Exception e) {
            return new ApiResponse(403, e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody UserRegisterRequest request) {
        try {
            UserVO userVO = userService.loginUser(request.getUsername(), request.getPassword());
            return new ApiResponse(200, "Login successful", userVO);
        } catch (Exception e) {
            return new ApiResponse(403, e.getMessage(), null);
        }
    }

    @GetMapping("/info/detail/{userId}")
    public ApiResponse getUserById(@PathVariable Long userId) {
        try {
            UserVO userVO = userService.getUserInfo(userId);
            return new ApiResponse(200, "User found", userVO);
        } catch (Exception e) {
            return new ApiResponse(403, e.getMessage(), null);
        }
    }
}
