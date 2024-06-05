package org.example.authenticator.onboard;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class OnBoardingViaMobileFactory implements AuthenticatorFactory {

    private static final String PROVIDER_ID = "registration using mobile number";
    private static final String DISPLAY_TYPE = "Registration Form";
    private static final String REFERENCE_CATEGORY = "Onboarding using SMS";
    private static final String HELP_TEXT = "Authenticator for mobile number registration.";

    @Override
    public Authenticator create(KeycloakSession session) {
        return new OnBoardingViaMobile();
    }

    @Override
    public void init(Config.Scope config) {
        // No initialization required
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post-initialization required
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
        return DISPLAY_TYPE;
    }

    @Override
    public String getReferenceCategory() {
        return REFERENCE_CATEGORY;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return HELP_TEXT;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create().build();
    }
}
