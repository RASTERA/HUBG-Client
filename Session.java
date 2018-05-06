public class Session {

    private String token, username;

    public Session(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return this.token;
    }

    public String getUsername() {
        return this.username;
    }
}