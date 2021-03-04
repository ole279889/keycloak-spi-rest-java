package mailru.rsst.test.spi;

import mailru.rsst.test.spi.entity.RequestingResetPasswordRequest;
import mailru.rsst.test.spi.entity.ResetPasswordRequest;
import mailru.rsst.test.spi.token.ResetCredentialsActionToken;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.credential.*;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.models.*;
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
    @Path("request-reset-password")
    @POST
    @Encoded
    @NoCache
    public void requestResetPassword(RequestingResetPasswordRequest resetPasswordRequest) {

        RealmModel realm = keycloakSession.getContext().getRealm();
        UriInfo uri = keycloakSession.getContext().getUri();
        UserModel userModel = keycloakSession.users().getUserByUsername(resetPasswordRequest.getUsername(), realm);

        if (userModel == null) {
            throw new NotFoundException("user with username " + resetPasswordRequest.getUsername() + " not found");
        }

        ResetCredentialsActionToken token = new ResetCredentialsActionToken(userModel.getId(), 1000);

        logger.info("uri " + uri);

        String tokenSerialized = token.serialize(keycloakSession, realm, uri);
        logger.info("token " + tokenSerialized);

        String email = userModel.getEmail();
        logger.info("email " + email);

        if (email != null) {
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
        } else {
            throw new InternalServerErrorException("user " + userModel.getUsername() + " has no email");
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @Path("reset-password")
    @POST
    @Encoded
    @NoCache
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {

        String tokenSerialized = resetPasswordRequest.getToken();
        String newPassword = resetPasswordRequest.getNewPassword();
        String confirmation = resetPasswordRequest.getConfirmation();

        TokenManager tokenManager = keycloakSession.tokens();
        ResetCredentialsActionToken token = tokenManager.decode(tokenSerialized, ResetCredentialsActionToken.class);

        RealmModel realm = keycloakSession.getContext().getRealm();
        UserModel userModel = keycloakSession.users().getUserById(token.getSubject(), realm);

        if (userModel != null && /*!token.isExpired() && */newPassword.equals(confirmation)) {

            PasswordCredentialProvider passwordProvider = (PasswordCredentialProvider) keycloakSession.getProvider(CredentialProvider.class, PasswordCredentialProviderFactory.PROVIDER_ID);
            passwordProvider.createCredential(realm, userModel, newPassword);

            logger.info("-->" + userModel.getUsername() + " password changed to " + newPassword);

        } else if (!newPassword.equals(confirmation)) {
            throw new BadRequestException("new password and confirmation are not equal");
        }
    }

    public Object getResource() {
        return this;
    }

    public void close() {

    }
}
