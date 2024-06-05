package org.example.authenticator.sms;

import jakarta.ws.rs.core.MultivaluedMap;
import org.example.authenticator.utils.OtpUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.example.authenticator.utils.Constants.*;
import static org.example.authenticator.utils.FailureChallenge.showError;

public class SMSAuthenticator implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(SMSAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        context.challenge(context.form().createForm(VERIFY_OTP_PAGE));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String otp = formParams.getFirst("otp");
        String resendOtp = formParams.getFirst("resend");
        String flowType = context.getAuthenticationSession().getAuthNote("FLOW_TYPE");

        switch (flowType) {
            case LOGIN_FLOW:
                logger.debug("login flow is running");
                handleOtpAction(context, otp, resendOtp, flowType, LOGIN_PAGE);
                break;
            case REGISTER_FLOW:
                logger.debug("register flow is running");
                handleOtpAction(context, otp, resendOtp, flowType, REGISTER_PAGE);
                break;
            case RESET_PASSWORD_FLOW:
                logger.debug("reset password flow is running");
                handleOtpAction(context, otp, resendOtp, flowType, FORGOT_PASSWORD_PAGE);
                break;
            default:
                showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Invalid flow type", VERIFY_OTP_PAGE);
                break;
        }
    }

    private void handleOtpAction(AuthenticationFlowContext context, String otp, String resend, String flowType, String form) {
        if (resend != null) {
            logger.debug("Resending otp for {}", flowType);
            handleOtpResend(context, form);
        } else if (otp != null) {
            logger.debug("validating otp for {}", flowType);
            handleOtpValidation(context, flowType, otp);
        } else {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "OTP required!", VERIFY_OTP_PAGE);
        }
    }

    private void handleOtpValidation(AuthenticationFlowContext context, String flowType, String otp) {
        String sessionOtp = context.getAuthenticationSession().getAuthNote(OTP_SESSION_ATTRIBUTE);
        String otpCreationTimeStr = context.getAuthenticationSession().getAuthNote(OTP_CREATION_TIME_ATTRIBUTE);

        if (sessionOtp == null || otpCreationTimeStr == null) {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INVALID_OTP, VERIFY_OTP_PAGE);
            return;
        }

        long otpCreationTime = Long.parseLong(otpCreationTimeStr);
        if (System.currentTimeMillis() - otpCreationTime > OTP_VALIDITY_DURATION) {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "OTP has expired. Please request a new OTP.", VERIFY_OTP_PAGE);
            return;
        }

        switch (flowType) {
            case LOGIN_FLOW:
            case RESET_PASSWORD_FLOW:
                UserModel authenticatedUser = context.getAuthenticationSession().getAuthenticatedUser();
                if (otp.equals(sessionOtp) && authenticatedUser != null) {
                    context.setUser(authenticatedUser);
                    context.success();
                } else {
                    showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INVALID_OTP, VERIFY_OTP_PAGE);
                }
                break;
            case REGISTER_FLOW:
                if (otp.equals(sessionOtp)) {
                    createUserAndAuthenticate(context);
                } else {
                    showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INVALID_OTP, VERIFY_OTP_PAGE);
                }
                break;
            default:
                showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Invalid flow type!", LOGIN_PAGE);
                break;
        }

    }

    private void createUserAndAuthenticate(AuthenticationFlowContext context) {
        String userName = context.getAuthenticationSession().getAuthNote("TEMP_USER_NAME");
        String password = context.getAuthenticationSession().getAuthNote("TEMP_PASSWORD");

        try {
            UserModel newUser = context.getSession().users().addUser(context.getRealm(), userName);
            newUser.setEnabled(true);
            newUser.credentialManager().updateCredential(UserCredentialModel.password(password, false));
            context.getAuthenticationSession().setAuthenticatedUser(newUser);
            context.success();
            logger.debug("User {} created successfully", userName);
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Internal server error", VERIFY_OTP_PAGE);
        }
    }

    private void handleOtpResend(AuthenticationFlowContext context, String form) {
        UserModel user = context.getAuthenticationSession().getAuthenticatedUser();

        if (user != null) {
            String mobileNumber = user.getFirstAttribute("mobileNumber");

            if (mobileNumber == null) {
                showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Mobile number is missing. Restart the login process.", form);
                return;
            }

            String generatedOtp = OtpUtils.generateOTP(6);
            boolean otpSent = OtpUtils.sendOTP(mobileNumber, generatedOtp, context, VERIFY_OTP_PAGE);

            if (otpSent) {
                context.getAuthenticationSession().setAuthNote(OTP_SESSION_ATTRIBUTE, generatedOtp);
                context.getAuthenticationSession().setAuthNote(OTP_CREATION_TIME_ATTRIBUTE, String.valueOf(System.currentTimeMillis()));
                context.challenge(context.form().setSuccess("OTP sent successfully").createForm(VERIFY_OTP_PAGE));
            } else {
                showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Internal Server Error, OTP sent failed.", VERIFY_OTP_PAGE);
            }
        } else {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "User not found or mobile number is missing", VERIFY_OTP_PAGE);
        }
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        // No required actions needed
    }

    @Override
    public void close() {
        // No resources to close
    }
}
