package com.app.user.controller;

import com.app.user.model.User;
import com.app.user.repository.UserRepository;
import com.app.user.util.JwtUtil;
import com.app.user.vo.UserVO;
import com.app.user.dto.UserRegisterRequest;
import com.app.user.dto.UserUpdateRequest;
import com.app.user.dto.ApiResponse;
import com.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;
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
    public ApiResponse update(@RequestBody UserUpdateRequest request, @RequestHeader("token") String token) {
        try {
            // 从 token 中解析出用户名
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return new ApiResponse(401, "无效的 Token", null);
            }
            // 通过用户名查找用户信息
            User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "无效的 Token"));

            // 使用查找到的 userId 进行用户信息更新
            UserVO userVO = userService.updateUser(user.getId(), request, token);

            return new ApiResponse(200, "Update successful", userVO);
        } catch (ResponseStatusException e) {
            return new ApiResponse(e.getStatus().value(), e.getReason(), null);
        } catch (Exception e) {
            return new ApiResponse(500, e.getMessage(), null);
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

    @GetMapping("/info/detail")
    public ApiResponse getUserInfo(@RequestHeader("token") String token) {
        try {
            // 使用 token 获取用户信息
            UserVO userVO = userService.getUserInfoByToken(token);
            return new ApiResponse(200, "User info retrieved successfully", userVO);
        } catch (ResponseStatusException e) {
            return new ApiResponse(e.getStatus().value(), e.getReason(), null);
        } catch (Exception e) {
            return new ApiResponse(500, e.getMessage(), null);
        }
    }
}
