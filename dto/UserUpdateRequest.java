package com.app.user.dto;

public class UserUpdateRequest {

    private String avatar;      // 头像图片的URL
    private String birthday; // Date as a String in "yyyy-MM-dd" format
    private String email;      // 邮箱
    private String slogan;      // 个性签名
    private String nickname;    // 昵称

    // Getters and setters
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
