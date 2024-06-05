package org.example.authenticator.utils;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class FindUser {
    private FindUser() {
        // intentionally set to empty
    }

    public static UserModel findUser(KeycloakSession session, RealmModel realm, String mobileNumber) {
        return session.users().getUserByUsername(realm, mobileNumber);
    }
}
