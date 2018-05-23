public class Session {

    private String token, email;

    public Session(String email, String token) {
        this.token = token;
        this.email = email;
    }

    public String getToken() {
        return this.token;
    }

    public String getEmail() {
        return this.email;
    }
}