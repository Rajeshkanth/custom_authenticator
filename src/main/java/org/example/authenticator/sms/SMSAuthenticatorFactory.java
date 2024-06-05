package org.example.authenticator.sms;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class SMSAuthenticatorFactory implements AuthenticatorFactory {
    public static final String PROVIDER_ID = "sms-form";

    @Override
    public Authenticator create(KeycloakSession session) {
        return new SMSAuthenticator();
    }

    @Override
    public void init(Config.Scope config) {
        // No initialization needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
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
        return "SMS Authenticator";
    }

    @Override
    public String getHelpText() {
        return "Authenticator for SMS functionalities";
    }

    @Override
    public String getReferenceCategory() {
        return "OTP";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create().build();
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }
}
