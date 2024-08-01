package com.app.user.service;

import com.app.user.dto.UserUpdateRequest;
import com.app.user.model.User;

public interface UserService {
    User registerUser(String username, String password) throws Exception;
    User getUserInfo(Long userId) throws Exception;
    User updateUser(Long userId, UserUpdateRequest userUpdateRequest) throws Exception;
    User loginUser(String username, String password) throws Exception;
}
