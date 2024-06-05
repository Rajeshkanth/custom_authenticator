package org.example.authenticator.login;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Collections;
import java.util.List;

public class MobileNumberAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "mobile-authenticator";

    @Override
    public Authenticator create(KeycloakSession session) {
        return new MobileNumberAuthenticator();
    }

    @Override
    public void init(Config.Scope scope) {
        // No initialization needed
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        // No post initialization needed
    }

    @Override
    public void close() {
        // No resources to close
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Login Form";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "Validates a mobile number and password from login form.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return Collections.emptyList();
    }
}