//package com.app.user.serviceImpl;
//
//import com.app.user.dto.UserUpdateRequest;
//import com.app.user.model.User;
//import com.app.user.model.UserRepository;
//import com.app.user.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.Optional;
//
//@Service
//public class UserServiceImplTTTT implements UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public User registerUser(String username, String password) throws Exception {
//        if (userRepository.findByUsername(username).isPresent()) {
//            throw new Exception("Username already exists");
//        }
//
//        User newUser = new User();
//        newUser.setUsername(username);
//        newUser.setPassword(password);
//        newUser.setCreatedAt(LocalDate.now()); // Set the creation date
//        newUser.setStatus("ACTIVATED"); // Set the user status
//
//        return userRepository.save(newUser);
//    }
//
//    @Override
//    public User getUserInfo(Long userId) throws Exception {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new Exception("User not found"));
//    }
//
//    @Override
//    public User updateUser(Long userId, UserUpdateRequest userUpdateRequest) throws Exception {
//        User user = getUserInfo(userId); // Reusing the getUserInfo method
//
//        // Convert birthday from String to LocalDate
//        try {
//            if (userUpdateRequest.getBirthday() != null && !userUpdateRequest.getBirthday().isEmpty()) {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                LocalDate birthday = LocalDate.parse(userUpdateRequest.getBirthday(), formatter);
//                user.setBirthday(birthday);
//            }
//        } catch (DateTimeParseException e) {
//            throw new Exception("Invalid date format for birthday");
//        }
//
//        // Update other fields
//        user.setAvatar(userUpdateRequest.getAvatar());
//        user.setEmail(userUpdateRequest.getEmail());
//        user.setSlogan(userUpdateRequest.getSlogan());
//        user.setNickname(userUpdateRequest.getNickname());
//        user.setBirthday(user.getBirthday());
//        user.setNickname(userUpdateRequest.getNickname());
//
//
//
//
//        return userRepository.save(user);
//    }
//
//    @Override
//    public User loginUser(String username, String password) throws Exception {
//        Optional<User> optionalUser = userRepository.findByUsername(username);
//        if (!optionalUser.isPresent() || !optionalUser.get().getPassword().equals(password)) {
//            throw new Exception("Invalid username or password");
//        }
//        return optionalUser.get();
//    }
//}
