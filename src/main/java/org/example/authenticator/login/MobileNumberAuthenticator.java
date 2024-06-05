package org.example.authenticator.login;

import jakarta.ws.rs.core.MultivaluedMap;
import org.example.authenticator.utils.OtpUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.*;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.PasswordCredentialProvider;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.authenticator.utils.Constants.*;
import static org.example.authenticator.utils.FailureChallenge.showError;
import static org.example.authenticator.utils.FindUser.findUser;

public class MobileNumberAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        boolean isRememberMeAllowed = context.getRealm().isRememberMe();
        context.form().setAttribute("isRememberMeAllowed", isRememberMeAllowed);
        context.form().setAttribute("login", context.getAuthenticationSession().getAuthenticatedUser());
        context.getAuthenticationSession().setAuthNote("FLOW_TYPE", LOGIN_FLOW);
        context.challenge(context.form().createForm(LOGIN_PAGE));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String mobileNumber = formParams.getFirst(MOBILE_NUMBER);
        String password = formParams.getFirst("password");

        if (isValidInput(mobileNumber, password)) {
            handleLoginForm(context, mobileNumber, password);
        } else {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Mobile number and password required", LOGIN_PAGE);
        }
    }

    private boolean isValidInput(String mobileNumber, String password) {
        return mobileNumber != null && password != null && !mobileNumber.isEmpty() && !password.isEmpty();
    }

    private void handleLoginForm(AuthenticationFlowContext context, String mobileNumber, String password) {
        UserModel user = findUser(context.getSession(), context.getRealm(), mobileNumber);
        if (user != null && validatePassword(context.getSession(), context.getRealm(), user, UserCredentialModel.password(password))) {
            processValidLogin(context, mobileNumber, user);
        } else {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Invalid credentials!", LOGIN_PAGE);
        }
    }

    private void processValidLogin(AuthenticationFlowContext context, String mobileNumber, UserModel user) {
        if (isSubFlowRequired(context)) {
            handleOtpFlow(context, mobileNumber, user);
        } else {
            authenticateUser(context, user);
        }
    }

    private void handleOtpFlow(AuthenticationFlowContext context, String mobileNumber, UserModel user) {
        String generatedOtp = OtpUtils.generateOTP(6);
        boolean otpSent = OtpUtils.sendOTP(mobileNumber, generatedOtp, context, LOGIN_PAGE);
        if (otpSent) {
            context.getAuthenticationSession().setAuthNote(OTP_SESSION_ATTRIBUTE, generatedOtp);
            context.getAuthenticationSession().setAuthNote(OTP_CREATION_TIME_ATTRIBUTE, String.valueOf(System.currentTimeMillis()));
            authenticateUser(context, user);
        } else {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Internal error", LOGIN_PAGE);
        }
    }

    private void authenticateUser(AuthenticationFlowContext context, UserModel user) {
        context.getAuthenticationSession().setAuthenticatedUser(user);
        context.success();
    }

    private boolean isSubFlowRequired(AuthenticationFlowContext context) {
        String currentExecutionId = context.getExecution().getId();
        AuthenticationExecutionModel currentExecution = context.getRealm().getAuthenticationExecutionById(currentExecutionId);
        String parentFlowId = currentExecution.getParentFlow();
        List<AuthenticationExecutionModel> executions = context.getRealm().getAuthenticationExecutionsStream(parentFlowId).collect(Collectors.toList());

        for (AuthenticationExecutionModel execution : executions) {
            if (execution.isAuthenticatorFlow() && execution.getRequirement() == AuthenticationExecutionModel.Requirement.REQUIRED) {
                return true;
            }
        }
        return false;
    }

    private boolean validatePassword(KeycloakSession session, RealmModel realm, UserModel user, CredentialInput password) {
        PasswordCredentialProvider passwordCredentialProvider = new PasswordCredentialProvider(session);
        return passwordCredentialProvider.isValid(realm, user, password);
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No required actions to set
    }

    @Override
    public void close() {
        // No resources to close
    }
}
