// PROJECT HUBG
// Henry Tu, Ryan Zhang, Syed Safwaan
// rastera.xyz
// 2018 ICS4U FINAL
//
// Session.java - Handles session data

package com.rastera.hubg.desktop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.Instant;

// Serializable AuthToken for saving local session
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

    public Session(String token, JSONObject user) {
        this.authToken = new AuthToken(token);
        this.user = user;

        // Generates user data from JSON if available
        if (this.user != null) {
            this.updateJSON();

            try {
                this.messages.put(new JSONObject() {
                    {
                        this.put("message", "[System] Welcome to HUBG Chat!");
                        this.put("time", Instant.now().toEpochMilli());
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    // Destroy session if invalid
    public static void destroySession() {
        Main.session = null;
        File file = new File("session.dat");
        file.delete();
    }

    // Save session to disk
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

    // Pulls session from disk
    public static AuthToken readSession() {
        try {
            FileInputStream fin = new FileInputStream("session.dat");
            ObjectInputStream inputStream = new ObjectInputStream(fin);
            return (AuthToken) inputStream.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public AuthToken getAuthToken() {
        return this.authToken;
    }

    // Updates profile
    public void updateJSON() {
        try {
            this.username = this.user.getString("username");
            this.skin = this.user.getString("skin");
        } catch (Exception e) {
            Main.errorQuit(e);
        }
    }

    // Get  core parameters
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