import org.json.JSONObject;

public class Session {

    private String token, email, username, skin;
    private int rank;
    public JSONObject user;

    public Session(String email, String token) {
        this.token = token;
        this.email = email;
        this.username = "";
    }

    public Session(String email, String token, JSONObject user) {
        this.token = token;
        this.email = email;
        this.user = user;

        updateJSON();
    }

    public void updateJSON() {
        try {
            this.rank = this.user.getInt("rank");
            this.username = this.user.getString("username");
            this.skin = this.user.getString("skin");
        } catch (Exception e) {
            Main.errorQuit(e);
        }
    }

    public int getRank() {
        return this.rank;
    }

    public String getSkin() {
        return this.skin;
    }

    public String getToken() {
        return this.token;
    }

    public String getUsername() {
        return this.username;
    }
}