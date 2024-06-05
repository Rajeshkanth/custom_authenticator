package org.example.authenticator.password;

import jakarta.ws.rs.core.MultivaluedMap;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

import static org.example.authenticator.utils.Constants.UPDATE_PASSWORD_PAGE;
import static org.example.authenticator.utils.FailureChallenge.showError;

public class UpdatePasswordAuthenticator implements Authenticator {
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        context.challenge(context.form().createForm("update-password.ftl"));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String password = formParams.getFirst("password");
        String confirmPassword = formParams.getFirst("confirmPassword");
        UserModel user = context.getUser();

        if (!confirmPassword.equals(password)) {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, "Password doesn't matches with confirm password", UPDATE_PASSWORD_PAGE);
        }

        user.credentialManager().updateCredential(UserCredentialModel.password(password, false));
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
//      not needed
    }

    @Override
    public void close() {
//      not needed
    }
}
