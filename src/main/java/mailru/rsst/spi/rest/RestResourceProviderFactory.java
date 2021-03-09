package mailru.rsst.spi.rest;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class RestResourceProviderFactory implements RealmResourceProviderFactory {

    private String REST_SPI_PROVIDER_ID = "rest-spi-provider";

    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        return new RestResourceProvider(keycloakSession);
    }

    public void init(Config.Scope scope) {

    }

    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    public void close() {

    }

    public String getId() {
        return REST_SPI_PROVIDER_ID;
    }
}
