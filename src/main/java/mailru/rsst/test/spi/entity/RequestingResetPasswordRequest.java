package mailru.rsst.test.spi.entity;

public class RequestingResetPasswordRequest {

    private String username;

    RequestingResetPasswordRequest() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
