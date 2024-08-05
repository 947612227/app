package com.app.user.serviceImpl;

import com.app.user.dto.UserUpdateRequest;
import com.app.user.model.User;
import com.app.user.repository.UserRepository;
import com.app.user.model.UserToken;
import com.app.user.repository.UserTokenRepository;
import com.app.user.service.UserService;
import com.app.user.util.JwtUtil;
import com.app.user.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserTokenRepository userTokenRepository;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public UserVO registerUser(String username, String password) throws Exception {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username already exists");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setStatus("ACTIVATED"); // Set the user status
        newUser.setCreatedAt(new Date()); // Set creation date

        User savedUser = userRepository.save(newUser);

        // Generate JWT token
        String token = jwtUtil.generateToken(username);

        // Save token to the database
        UserToken userToken = new UserToken();
        userToken.setToken(token);
        userToken.setCreatedAt(new Date());
        userToken.setUpdatedAt(new Date()); // Set updatedAt when creating
        userToken.setUser(savedUser);
        userTokenRepository.save(userToken);

        // Convert user to UserVO and include the token
        UserVO userVO = convertToVO(savedUser);
        userVO.setToken(token);

        return userVO;
    }

    @Override
    public UserVO getUserInfo(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("用户不存在!"));
        String token = jwtUtil.generateToken(user.getUsername()); // Optional based on your logic
        UserVO userVO = convertToVO(user);
        userVO.setToken(token); // Set the token in UserVO
        return userVO;
    }

    @Override
    public UserVO loginUser(String username, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent() || !optionalUser.get().getPassword().equals(password)) {
            throw new Exception("Invalid username or password");
        }

        User user = optionalUser.get();
        String token = jwtUtil.generateToken(username);

        // Find existing token for the user
        UserToken existingToken = userTokenRepository.findByUser(user);

        if (existingToken != null) {
            // Update existing token
            existingToken.setToken(token);
            existingToken.setUpdatedAt(new Date()); // Update the timestamp
            userTokenRepository.save(existingToken);
        } else {
            // Save new token
            UserToken userToken = new UserToken();
            userToken.setToken(token);
            userToken.setCreatedAt(new Date());
            userToken.setUpdatedAt(new Date()); // Set updatedAt when creating
            userToken.setUser(user);
            userTokenRepository.save(userToken);
        }

        // Convert user to UserVO and include the token
        UserVO userVO = convertToVO(user);
        userVO.setToken(token);

        return userVO;
    }

    @Override
    public UserVO updateUser(Long userId, UserUpdateRequest userUpdateRequest, String token) throws Exception {
        // 验证 token 是否有效
        if (!jwtUtil.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "无效的Token");
        }

        // 从 token 中获取用户名
        String usernameFromToken = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("用户不存在!"));

        // 确保 token 对应的用户是当前更新的用户
        if (!user.getUsername().equals(usernameFromToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "无效的Token");
        }

        // 更新生日信息
        try {
            if (userUpdateRequest.getBirthday() != null && !userUpdateRequest.getBirthday().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate birthday = LocalDate.parse(userUpdateRequest.getBirthday(), formatter);
                user.setBirthday(birthday);
            }
        } catch (DateTimeParseException e) {
            throw new Exception("生日格式错误!应当为yyyy-MM-dd格式");
        }

        // 更新其他字段
        user.setAvatar(userUpdateRequest.getAvatar());
        user.setEmail(userUpdateRequest.getEmail());
        user.setSlogan(userUpdateRequest.getSlogan());
        user.setNickname(userUpdateRequest.getNickname());
        user.setStatus(userUpdateRequest.getStatus());
        user.setCountryCode(userUpdateRequest.getCountryCode());

        // 更新更新时间戳
        user.setUpdatedAt(new Date());

        // 保存更新后的用户
        User updatedUser = userRepository.save(user);

        // 获取现有 token
        Optional<UserToken> userTokenOptional = userTokenRepository.findByUserId(userId);
        String existingToken = userTokenOptional.map(UserToken::getToken).orElse(null);

        // 转换为 VO 并设置 token
        UserVO userVO = convertToVO(updatedUser);
        userVO.setToken(existingToken);

        return userVO;
    }

    // Private helper method to convert User entity to UserVO
    private UserVO convertToVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setAvatar(user.getAvatar());
        userVO.setBirthday(user.getBirthday() != null ? user.getBirthday().toString() : null);
        userVO.setCountryCode(user.getCountryCode());
        String dateStr = user.getCreatedAt() != null ? dateFormat.format(user.getCreatedAt()) : null;
        userVO.setCreatedAt(dateStr);
        userVO.setEmail(user.getEmail());
        userVO.setNickname(user.getNickname());
        userVO.setIsAdult(user.getIsAdult());
        userVO.setSlogan(user.getSlogan());
        userVO.setStatus(user.getStatus());
        return userVO;
    }
}

