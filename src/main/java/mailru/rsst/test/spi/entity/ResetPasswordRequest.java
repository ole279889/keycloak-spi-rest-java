package mailru.rsst.test.spi.entity;

public class ResetPasswordRequest {

    private String username;

    ResetPasswordRequest() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
