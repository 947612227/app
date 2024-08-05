package com.app.user.service;

import com.app.user.dto.UserUpdateRequest;
import com.app.user.vo.UserVO;

public interface UserService {
    UserVO registerUser(String username, String password) throws Exception;
    UserVO getUserInfo(Long userId) throws Exception;
    UserVO updateUser(Long userId, UserUpdateRequest userUpdateRequest, String token) throws Exception;
    UserVO loginUser(String username, String password) throws Exception;
}
