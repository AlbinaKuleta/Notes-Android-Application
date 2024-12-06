package com.example.aplikacionandroid;

public class ReadWriteUserDetails {
    public String doB, gender, mobile;

    /**
     * Default constructor.
     * Required for creating an instance without initializing fields.
     */
    public ReadWriteUserDetails() {
    }

    ;

    /**
     * Parameterized constructor.
     * Initializes the user details with the provided values.
     *
     * @param textDoB    The date of birth of the user.
     * @param textGender The gender of the user.
     * @param textMobile The mobile number of the user.
     */
    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile) {
        this.doB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
