package mailru.rsst.test.spi;

import mailru.rsst.test.spi.entity.ResetPasswordRequest;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) throws EmailException {

        logger.info("request " + resetPasswordRequest);
        RealmModel realm = keycloakSession.getContext().getRealm();
        UriInfo uri = keycloakSession.getContext().getUri();
        UserModel userModel = keycloakSession.users().getUserByUsername(resetPasswordRequest.getUsername(), realm);

        if (userModel == null) {
            throw new NotFoundException("user with username " + resetPasswordRequest.getUsername() + " not found");
        }

        ResetCredentialsActionToken token = new ResetCredentialsActionToken(userModel.getId(), 1000);
        String tokenSerialized = token.serialize(keycloakSession, realm, uri);
        logger.info("token " + tokenSerialized);

        String email = userModel.getEmail();
        logger.info("email " + email);

        if (userModel != null && email != null) {
            DefaultEmailSenderProvider senderProvider = new DefaultEmailSenderProvider(keycloakSession);
            Map<String, String> smtpConfig = keycloakSession.getContext().getRealm().getSmtpConfig();
            try {
                senderProvider.send(
                        smtpConfig,
                        userModel,
                        "смена пароля",
                        "для смены пароля перейдите по ссылке: http://www.smth.ru/reset-password/" + tokenSerialized,
                        "для смены пароля перейдите по ссылке: <a href=\"http://www.smth.ru/reset-password/" + tokenSerialized + "\">сменить пароль</a>"
                );
            } catch (EmailException e) {
                throw new InternalServerErrorException("error sending email to " + email);
            }
        }

        //userModel.addRequiredAction(UserModel.RequiredAction.UPDATE_PASSWORD);
        //logger.info("added required action");
    }

    public Object getResource() {
        return this;
    }

    public void close() {

    }
}
