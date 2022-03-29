package org.platform.pst.amt.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.Response;

public class MpiAuthenticator implements Authenticator {

    private static final Logger logger = Logger.getLogger(MpiAuthenticator.class);


    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {

        UserModel user = authenticationFlowContext.getUser();

        Boolean isUserVerified;
        if (DynamoDb.checkEmail(user.getEmail())) isUserVerified = true;
        else isUserVerified = false;

        if(!isUserVerified){
            String errorMessage = "Authentication failed";
            Response challengeResponse = authenticationFlowContext.form().setError(errorMessage).createErrorPage(Response.Status.FORBIDDEN);
            authenticationFlowContext.failure(AuthenticationFlowError.USER_TEMPORARILY_DISABLED, challengeResponse);
            logger.info("login failure " + user.getEmail());
            return;
        }
        authenticationFlowContext.success();

    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {

    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {

    }

    @Override
    public void close() {

    }

}
