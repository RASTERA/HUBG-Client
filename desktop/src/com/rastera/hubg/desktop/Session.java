package com.rastera.hubg.desktop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.Instant;

class AuthToken implements Serializable {
    private String token;

    public AuthToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

public class Session {

    private String username;
    private AuthToken authToken;
    private String skin;
    public JSONObject user;
    public JSONArray messages = new JSONArray();

    public Session(String token) {
        this.authToken = new AuthToken(token);
        this.username = "";
    }

    public Session(String token, JSONObject user) {
        this.authToken = new AuthToken(token);
        this.user = user;

        if (this.user != null) {
            this.updateJSON();

            try {
                messages.put(new JSONObject() {
                    {
                        put("message", "[System] Welcome to HUBG Chat!");
                        put("time", Instant.now().toEpochMilli());
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    public static void destroySession() {
        Main.session = null;
        File file = new File("session.dat");
        file.delete();
    }

    public static void writeSession() {
        try {
            FileOutputStream fout = new FileOutputStream("session.dat");
            ObjectOutputStream outputStream = new ObjectOutputStream(fout);
            outputStream.writeObject(Main.session.getAuthToken());
            outputStream.close();
        } catch (Exception e) {
            Main.errorQuit(e);
        }
    }

    public static AuthToken readSession() {
        try {
            FileInputStream fin = new FileInputStream("session.dat");
            ObjectInputStream inputStream = new ObjectInputStream(fin);
            return (AuthToken) inputStream.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return this.authToken;
    }

    public void setToken(String token) {
        this.authToken.setToken(token);
    }

    public void updateJSON() {
        try {
            this.username = this.user.getString("username");
            this.skin = this.user.getString("skin");
        } catch (Exception e) {
            Main.errorQuit(e);
        }
    }

    public Long getMoney() {
        try {
            return this.user.getLong("money");
        } catch (Exception e) {
            Main.errorQuit(e);
        }
        return null;
    }

    public String getSkin() {
        return this.skin;
    }

    public String getToken() {
        return this.authToken.getToken();
    }

    public String getUsername() {
        return this.username;
    }
}