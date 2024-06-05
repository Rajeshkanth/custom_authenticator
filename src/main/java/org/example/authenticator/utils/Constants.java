package org.example.authenticator.utils;

public class Constants {

    private Constants() {
        // intentionally set to empty
    }

    //    flow_types
    public static final String LOGIN_FLOW = "login";
    public static final String REGISTER_FLOW = "register";
    public static final String RESET_PASSWORD_FLOW = "resetPassword";

    //    SMS
    public static final String OTP_SESSION_ATTRIBUTE = "otp";
    public static final String OTP_CREATION_TIME_ATTRIBUTE = "otpCreationTime";
    public static final long OTP_VALIDITY_DURATION = 60000;
    public static final String INVALID_OTP = "Invalid OTP";
    public static final String MOBILE_NUMBER = "mobileNumber";

    //     forms
    public static final String VERIFY_OTP_PAGE = "verify-otp.ftl";
    public static final String REGISTER_PAGE = "register.ftl";
    public static final String LOGIN_PAGE = "login.ftl";
    public static final String FORGOT_PASSWORD_PAGE = "forgot-password.ftl";
    public static final String UPDATE_PASSWORD_PAGE = "update-password.ftl";

}
