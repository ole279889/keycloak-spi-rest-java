package mailru.rsst.test.spi.entity;

public class ResetPasswordRequest {

    private String token;
    private String newPassword;
    private String confirmation;

    ResetPasswordRequest() {}

    public String getToken() {
        return token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmation() {
        return confirmation;
    }
}
