package mailru.rsst.test.spi;


import org.keycloak.authentication.actiontoken.DefaultActionToken;

public class ResetCredentialsActionToken extends DefaultActionToken {

    public static final String TOKEN_TYPE = "reset-credentials";

    public ResetCredentialsActionToken(String userId, int absoluteExpirationInSecs) {
        super(userId, TOKEN_TYPE, absoluteExpirationInSecs, null);
    }

    private ResetCredentialsActionToken() {
    }
}
