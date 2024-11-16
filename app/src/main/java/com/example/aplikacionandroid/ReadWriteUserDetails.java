package com.example.aplikacionandroid;

public class ReadWriteUserDetails {
    public String fullName, doB, gender, mobile;
public ReadWriteUserDetails(){};
    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile){
       this.doB = textDoB;
       this.gender = textGender;
       this.mobile = textMobile;
    }
}
