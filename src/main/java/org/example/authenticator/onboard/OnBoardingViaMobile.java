package org.example.authenticator.onboard;

import jakarta.ws.rs.core.MultivaluedMap;
import org.example.authenticator.utils.OtpUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.example.authenticator.utils.Constants.*;
import static org.example.authenticator.utils.FailureChallenge.showError;
import static org.example.authenticator.utils.FindUser.findUser;

public class OnBoardingViaMobile implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(OnBoardingViaMobile.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        context.getAuthenticationSession().setAuthNote("FLOW_TYPE", REGISTER_FLOW);
        boolean isRememberMeEnabled = context.getRealm().isRememberMe();
        context.form().setAttribute("isRememberMeAllowed", isRememberMeEnabled);
        context.form().setAttribute("login", context.getAuthenticationSession().getAuthenticatedUser());
        context.challenge(context.form().createForm(REGISTER_PAGE));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String mobileNumber = formParams.getFirst(MOBILE_NUMBER);
        String password = formParams.getFirst("password");
        String confirmPassword = formParams.getFirst("confirmPassword");

        if (isFormIncomplete(mobileNumber, password, confirmPassword)) {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Fill all the fields.", REGISTER_PAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Entered password doesn't match confirm password!", REGISTER_PAGE);
            return;
        }

        if (findUser(context.getSession(), context.getRealm(), mobileNumber) != null) {
            showError(context, AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR, "Mobile number already exists!", REGISTER_PAGE);
            return;
        }

        processOTP(context, mobileNumber, password);
    }

    private boolean isFormIncomplete(String mobileNumber, String password, String confirmPassword) {
        return mobileNumber == null || mobileNumber.isEmpty() ||
                password == null || password.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty();
    }

    private void processOTP(AuthenticationFlowContext context, String mobileNumber, String password) {
        try {
            String generatedOtp = OtpUtils.generateOTP(6);
            boolean otpSent = OtpUtils.sendOTP(mobileNumber, generatedOtp, context, REGISTER_PAGE);
            if (otpSent) {
                storeTemporaryUserData(context, mobileNumber, password, generatedOtp);
                context.challenge(context.form().setSuccess("OTP sent successfully").createForm(VERIFY_OTP_PAGE));
                context.success();
            } else {
                showError(context, AuthenticationFlowError.INTERNAL_ERROR, "Internal Server Error, OTP sent failed.", REGISTER_PAGE);
            }
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            showError(context, AuthenticationFlowError.INTERNAL_ERROR, "Internal server error", REGISTER_PAGE);
        }
    }

    private void storeTemporaryUserData(AuthenticationFlowContext context, String mobileNumber, String password, String generatedOtp) {
        context.getAuthenticationSession().setAuthNote(OTP_SESSION_ATTRIBUTE, generatedOtp);
        context.getAuthenticationSession().setAuthNote(OTP_CREATION_TIME_ATTRIBUTE, String.valueOf(System.currentTimeMillis()));
        context.getAuthenticationSession().setAuthNote("TEMP_USER_NAME", mobileNumber);
        context.getAuthenticationSession().setAuthNote("TEMP_PASSWORD", password);
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
        // No required actions
    }

    @Override
    public void close() {
        // No resources to close
    }
}
