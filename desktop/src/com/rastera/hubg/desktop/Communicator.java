package com.rastera.hubg.desktop;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import javax.swing.border.Border;

public class Communicator {

    private static final boolean developmentMode = !true;

    private static final HashMap<RequestDestination, String> baseProductionHashMap = new HashMap<>() {
        {
            put(RequestDestination.URL, "https://rastera.xyz/");
            put(RequestDestination.API, "https://api.rastera.xyz/");
            put(RequestDestination.AUTH, "https://authentication.rastera.xyz/");
        }
    };

    private static final HashMap<RequestDestination, String> baseDevelopmentHashMap = new HashMap<>() {
        {
            put(RequestDestination.URL, "http://localhost:3005/");
            put(RequestDestination.API, "http://localhost:3005/api/");
            put(RequestDestination.AUTH, "http://localhost:3005/auth/");
        }
    };

    public enum RequestType {POST, GET}
    public enum RequestDestination {URL, API, AUTH}

    public static String getURL(RequestDestination destination) {
        if (Communicator.developmentMode) {
            return baseDevelopmentHashMap.get(destination);
        } else {
            return baseProductionHashMap.get(destination);
        }
    }

    public static JSONObject getMessages() {
        try {
            return request(RequestType.GET, null, Communicator.getURL(RequestDestination.API) + "messages/" + Main.session.getToken());
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        return null;
    }

    public static JSONObject sendMessage(String message) {
        try {
            JSONObject data = new JSONObject() {
                {
                    put("token", Main.session.getToken());
                    put("message", message);
                }
            };

            return request(RequestType.POST, data, Communicator.getURL(RequestDestination.API) + "chat");
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        return null;
    }

    public static JSONObject request(RequestType type, JSONObject data, String destination) {
        try {
            // Init connection

            URLConnection socket;
            if (Communicator.developmentMode) {
                socket = new URL(destination).openConnection();
                ((HttpURLConnection) socket).setRequestMethod(type.toString());
            } else {
                socket = new URL(destination).openConnection();
                ((HttpsURLConnection) socket).setRequestMethod(type.toString());
            }

            // Header stuff
            socket.setConnectTimeout(5000);
            socket.setRequestProperty("User-Agent", "Mozilla/5.0");
            socket.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            socket.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            if (type == RequestType.POST) {
                socket.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());

                writer.write(data.toString());
                writer.flush();
                writer.close();
            }

            //System.out.println("Getting Response of " + socket.getResponseCode());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String rawData = "";
            String line;

            while ((line = reader.readLine()) != null) {
                rawData += line;
            }

            return new JSONObject(rawData);

        } catch (Exception e) {
            e.printStackTrace();
            Main.errorQuit("Unable to process request. Please try again later.");
            return null;
        }
    }

    public static String shopRequest(JFrame parent, String type, String item) {

        try {
            JSONObject request = new JSONObject() {
                {
                    this.put("type", type);
                    this.put("item", item);
                    this.put("token", Main.session.getToken());
                }
            };

            JSONObject inJSON = Communicator.request(RequestType.POST, request, Communicator.getURL(RequestDestination.API) + "shopItem/");

            if (inJSON.has("success")) {
                return "ok";
            } else {
                return inJSON.getString("error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Main.errorQuit("Unable to process request. Please try again later.");
            return null;
        }
    }

    public static JSONObject getShop() {
        return Communicator.request(RequestType.GET, null, Communicator.getURL(RequestDestination.API) + "shop/");
    }

    public static JSONObject refresh(String token) {
        try {

            JSONObject dataJSON = Communicator.request(RequestType.GET, null, Communicator.getURL(RequestDestination.API) + "refresh/" + token);

            if (dataJSON.has("error")) {
                Main.errorQuit(dataJSON.getString("error"));
                return null;
            }

            Main.usersOnline = dataJSON.getInt("online");

            return dataJSON.getJSONObject("user");

        } catch (Exception e) {
            Main.errorQuit("Unable to synchronize with server. Please try again later.");
            return null;
        }
    }

    public static Session login(JSONObject credentials) {
        try {
            JSONObject dataJSON = Communicator.request(RequestType.POST, credentials, Communicator.getURL(RequestDestination.AUTH) + "login/");

            if (dataJSON.has("error")) {
                System.out.println(dataJSON.getString("error"));
                return null;
            }

            return new Session(dataJSON.getString("token"), dataJSON.getJSONObject("user"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Session login(AuthToken token) {
        try {

            JSONObject credentials = new JSONObject() {
                {
                    this.put("token", token.getToken());
                }
            };

            return login(credentials);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Session login(String email, String password) {
        try {

            JSONObject credentials = new JSONObject() {
                {
                    this.put("email", email);
                    this.put("password", password);
                }
            };

            return login(credentials);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}