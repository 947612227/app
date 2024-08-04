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
        User savedUser = userRepository.save(newUser);
        savedUser.setCreatedAt(new Date());

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
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("用户不存在!"));
        String token = jwtUtil.generateToken(user.getUsername());
        // Convert user to UserVO and include the token
        UserVO userVO = convertToVO(user);
        userVO.setToken(token); // Set the token in UserVO

        return userVO;
//        return convertToVO(user);
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
    public UserVO updateUser(Long userId, UserUpdateRequest userUpdateRequest) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("用户不存在!"));

        // Convert birthday from String to LocalDate
        try {
            if (userUpdateRequest.getBirthday() != null && !userUpdateRequest.getBirthday().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate birthday = LocalDate.parse(userUpdateRequest.getBirthday(), formatter);
                user.setBirthday(birthday);
            }
        } catch (DateTimeParseException e) {
            throw new Exception("生日格式错误!应当为yyyy-MM-dd格式");
        }

        // Update other fields
        user.setAvatar(userUpdateRequest.getAvatar());
        user.setEmail(userUpdateRequest.getEmail());
        user.setSlogan(userUpdateRequest.getSlogan());
        user.setNickname(userUpdateRequest.getNickname());
        user.setStatus(userUpdateRequest.getStatus());
        user.setCountryCode(userUpdateRequest.getCountryCode());



        // Save the updated user and return the corresponding VO
        User updatedUser = userRepository.save(user);
        return convertToVO(updatedUser);
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
