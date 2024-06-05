package org.example.authenticator.password;

import jakarta.ws.rs.core.MultivaluedMap;
import org.example.authenticator.utils.OtpUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import static org.example.authenticator.utils.Constants.*;
import static org.example.authenticator.utils.FailureChallenge.showError;
import static org.example.authenticator.utils.FindUser.findUser;

public class ResetCredentialAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        context.getAuthenticationSession().setAuthNote("FLOW_TYPE", RESET_PASSWORD_FLOW);
        context.challenge(context.form().createForm(FORGOT_PASSWORD_PAGE));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String mobileNumber = formParams.getFirst(MOBILE_NUMBER);
        UserModel existingUser = findUser(context.getSession(), context.getRealm(), mobileNumber);

        if (existingUser != null) {
            String otp = OtpUtils.generateOTP(6);
            boolean otpSent = OtpUtils.sendOTP(mobileNumber, otp, context, FORGOT_PASSWORD_PAGE);

            if (otpSent) {
                context.getAuthenticationSession().setAuthNote(OTP_SESSION_ATTRIBUTE, otp);
                context.getAuthenticationSession().setAuthNote(OTP_CREATION_TIME_ATTRIBUTE, String.valueOf(System.currentTimeMillis()));
                authenticateUser(context, existingUser);
            } else {
                showError(context, AuthenticationFlowError.INVALID_USER, "Failed to send OTP. Please try again.", FORGOT_PASSWORD_PAGE);
            }
        } else {
            showError(context, AuthenticationFlowError.INVALID_USER, "User not found.", FORGOT_PASSWORD_PAGE);
        }
    }

    private void authenticateUser(AuthenticationFlowContext context, UserModel user) {
        context.getAuthenticationSession().setAuthenticatedUser(user);
        context.success();
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
        // Not needed
    }

    @Override
    public void close() {
        // Not needed
    }
}
