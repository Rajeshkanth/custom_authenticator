package org.example.authenticator.utils;

import com.twilio.Twilio;
import com.twilio.exception.TwilioException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;

import java.util.Random;

public class OtpUtils {

    private OtpUtils() {
        // intentionally set to empty
    }

    private static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    private static final String TWILIO_PHONE_NUMBER = System.getenv("TWILIO_PHONE_NUMBER");
    private static final Random random = new Random();


    public static String generateOTP(int length) {
        String numbers = "0123456789";
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(numbers.length());
            char digit = numbers.charAt(index);
            otp.append(digit);
        }
        return otp.toString();
    }

    public static boolean sendOTP(String mobileNumber, String otp, AuthenticationFlowContext context, String form) throws TwilioException {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            String messageBody = "Your OTP for Easy Transfer login is: " + otp;
            Message.creator(
                    new PhoneNumber(mobileNumber),
                    new PhoneNumber(TWILIO_PHONE_NUMBER),
                    messageBody
            ).create();
            return true;
        } catch (Exception e) {
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, context.form().setError("Internal server error").createForm(form));
            return false;
        }
    }
}
