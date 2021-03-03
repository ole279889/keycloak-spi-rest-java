package mailru.rsst.test.spi;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestResourceProvider implements RealmResourceProvider {

    private KeycloakSession keycloakSession;
    private final Logger logger = LoggerFactory.getLogger(TestResourceProvider.class);

    public TestResourceProvider(KeycloakSession keycloakSession) {
        this.keycloakSession = keycloakSession;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("reset-password")
    @POST
    @Encoded
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        logger.info("request " + resetPasswordRequest);
        RealmModel realm = keycloakSession.getContext().getRealm();
        UserModel userModel = keycloakSession.users().getUserByUsername(resetPasswordRequest.getUsername(), realm);
        if (userModel == null) {
            throw new NotFoundException("user with username " + resetPasswordRequest.getUsername() + " not found");
        }
        logger.info("user " + userModel.getId() + " " + userModel.getUsername());
        userModel.addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
        logger.info("added required action");
    }

    public Object getResource() {
        return this;
    }

    public void close() {

    }
}
