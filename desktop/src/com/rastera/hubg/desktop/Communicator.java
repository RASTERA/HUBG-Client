// PROJECT HUBG
// Henry Tu, Ryan Zhang, Syed Safwaan
// rastera.xyz
// 2018 ICS4U FINAL
//
// Communicator.java - Handles core networking

package com.rastera.hubg.desktop;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.rastera.Networking.Message;
import com.rastera.hubg.Screens.HUBGGame;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;

public class Communicator {

    // Main game network streams
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Message queue
    private LinkedBlockingQueue<Message> message;

    // Information about host
    private String serverName;
    private boolean listening = true;

    // Toggles between production and development mode
    public static final boolean developmentMode = !true;

    // Get URL based on development mode
    public static String getURL(RequestDestination destination) {
        if (Communicator.developmentMode) {
            return baseDevelopmentHashMap.get(destination);
        } else {
            return baseProductionHashMap.get(destination);
        }
    }

    // Dynamically changes URL based on development state
    private static final HashMap<RequestDestination, String> baseProductionHashMap = new HashMap<>() {
        {
            this.put(RequestDestination.URL, "https://rastera.xyz/");
            this.put(RequestDestination.API, "https://api.rastera.xyz/");
            this.put(RequestDestination.AUTH, "https://authentication.rastera.xyz/");
        }
    };

    private static final HashMap<RequestDestination, String> baseDevelopmentHashMap = new HashMap<>() {
        {
            this.put(RequestDestination.URL, "http://localhost:3005/");
            this.put(RequestDestination.API, "http://localhost:3005/api/");
            this.put(RequestDestination.AUTH, "http://localhost:3005/auth/");
        }
    };

    public enum RequestType {POST, GET}
    public enum RequestDestination {URL, API, AUTH}

    // Game object
    public HUBGGame client;

    // Communicator object for in game networking
    public Communicator(String ip, int port, final HUBGGame client) throws Exception {

        this.client = client;

        System.out.println("Connecting...");

        //this.serverSock.setSoTimeout(1000);
        //Socket serverSock = new Socket(InetAddress.getByName(ip), port);
        Socket serverSock = new Socket(InetAddress.getByAddress(new byte[] {127,0,0,1}), port);

        // Network object streams
        this.out = new ObjectOutputStream(serverSock.getOutputStream());
        this.in = new ObjectInputStream(serverSock.getInputStream());

        System.out.println("Connected to server: " + ip + ":" + port);

        // Thread listening for incoming messages
        Thread receiver = new Thread(() -> {
            while (this.listening) {
                try {
                    // Creates message object and queues to process
                    Message msg = (Message) this.in.readObject();

                    client.CommandProcessor(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                    //Main.errorQuit("Disconnected from server");
                }
            }
        });

        receiver.setDaemon(true);
        receiver.start();

        // Request server name
        this.write(-1, null);

    }

    // Write to server
    public void write(int type, Object Message) {
        try {
            this.out.writeObject(Util.messageBuilder(type, Message));
        } catch (Exception e) {
            e.printStackTrace();
            Main.errorQuit("Disconnected from server");
        }
    }

    // Get chat messages
    public static JSONObject getMessages() {
        try {
            return request(RequestType.GET, null, Communicator.getURL(RequestDestination.API) + "messages/" + Main.session.getToken());
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        return null;
    }

    // Send chat message
    public static JSONObject sendMessage(String message) {
        try {
            JSONObject data = new JSONObject() {
                {
                    this.put("token", Main.session.getToken());
                    this.put("message", message);
                }
            };

            return request(RequestType.POST, data, Communicator.getURL(RequestDestination.API) + "chat");
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        return null;
    }

    // General request method
    // Sends POST or GET request based on parameter
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

            // Config header
            socket.setConnectTimeout(5000);
            socket.setRequestProperty("User-Agent", "Mozilla/5.0");
            socket.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            socket.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Flushes JSON object if POST
            if (type == RequestType.POST) {
                socket.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());

                writer.write(data.toString());
                writer.flush();
                writer.close();
            }

            // Reads response
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            StringBuilder rawData = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                rawData.append(line);
            }

            return new JSONObject(rawData.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Main.errorQuit("Unable to process request. Please try again later.");
            return null;
        }
    }

    // Submits request for shop
    // Ex. Switch item or purchasing items
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

    // Get shop contents
    public static JSONObject getShop() {
        return Communicator.request(RequestType.GET, null, Communicator.getURL(RequestDestination.API) + "shop/");
    }

    // Refresh launcher statistics
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

    // Handle login and authentication
    public static Session login(JSONObject credentials) {
        try {
            JSONObject dataJSON = Communicator.request(RequestType.POST, credentials, Communicator.getURL(RequestDestination.AUTH) + "login/");

            // If error is returned from server, return special token
            if (dataJSON.has("error")) {
                System.out.println(dataJSON.getString("error"));
                return new Session(dataJSON.getString("error"), null);
            }

            // Gets token if available
            return new Session(dataJSON.getString("token"), dataJSON.getJSONObject("user"));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Login with token/session
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

    // Login with traditional credentials
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

    // Gets auth token for server handshake
    public static String getServerAuthToken(String serverName) {

        try {
            JSONObject data = Communicator.request(Communicator.RequestType.GET, null, Communicator.getURL(Communicator.RequestDestination.AUTH) + String.format("getGameAuth/%s/%s/", Main.session.getAuthToken().getToken(), serverName));

            if (data.has("error")) {
                Main.errorQuit(data.getString("error"));
            }

            return data.getString("token");

        } catch (Exception e) {
            Main.errorQuit(e);
        }

        return null;
    }

}