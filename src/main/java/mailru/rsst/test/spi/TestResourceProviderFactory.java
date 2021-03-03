package mailru.rsst.test.spi;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class TestResourceProviderFactory implements RealmResourceProviderFactory {

    private String TEST_REST_PROVIDER_ID = "test-rest-provider";

    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        return new TestResourceProvider(keycloakSession);
    }

    public void init(Config.Scope scope) {

    }

    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    public void close() {

    }

    public String getId() {
        return TEST_REST_PROVIDER_ID;
    }
}
