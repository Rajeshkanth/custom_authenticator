package org.example.authenticator.utils;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;

public class FailureChallenge {

    private FailureChallenge() {
        /* intentionally set to empty */
    }

    public static void showError(AuthenticationFlowContext context, AuthenticationFlowError flowError, String errorMessage, String form) {
        context.failureChallenge(flowError, context.form().setError(errorMessage).createForm(form));
    }

}
