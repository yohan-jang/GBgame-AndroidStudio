package com.example.good_bad_game;

import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("mail")
    private String mail;

    @SerializedName("password")
    private String password;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("userid")
    private String userid;

    public Post(String ipt_mail, String ipt_password, String ipt_nickname, String ipt_name, String ipt_phone, String ipt_userid) {
        this.mail = ipt_mail;
        this.password = ipt_password;
        this.nickname = ipt_nickname;
        this.name = ipt_name;
        this.phone = ipt_phone;
        this.userid = ipt_userid;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserid() {
        return userid;
    }
}
