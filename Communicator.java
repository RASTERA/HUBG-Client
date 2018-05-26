import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

public class Communicator {

    public static JSONObject refresh(String token) {
        try {

            // Init connection
            HttpsURLConnection socket = (HttpsURLConnection) new URL("https://api.rastera.xyz/refresh/" + token).openConnection();
            //HttpURLConnection socket = (HttpURLConnection) new URL("http://localhost:3005/api/refresh/" + token).openConnection();

            // Header stuff
            socket.setRequestMethod("GET");
            socket.setConnectTimeout(5000);
            socket.setRequestProperty("User-Agent", "Mozilla/5.0");
            socket.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            socket.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            System.out.println("Getting Response of " + socket.getResponseCode());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String data = reader.readLine().trim();

            JSONObject dataJSON = new JSONObject(data);

            System.out.println(data);

            if (dataJSON.has("error")) {
                return null;
            }

            return dataJSON.getJSONObject("user");

        } catch (Exception e) {
            Main.errorQuit("Unable to synchronize with server. Please try again later.");

            e.printStackTrace();
            System.out.println("lol something went wrong");
            return null;
        }
    }


    public static Session login(String email, String password) {
        try {

            // Init connection
            HttpsURLConnection socket = (HttpsURLConnection) new URL("https://authentication.rastera.xyz/login").openConnection();
            //HttpURLConnection socket = (HttpURLConnection) new URL("http://localhost:3005/auth/login").openConnection();

            // Header stuff
            socket.setRequestMethod("POST");
            socket.setConnectTimeout(5000);
            socket.setRequestProperty("User-Agent", "Mozilla/5.0");
            socket.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            socket.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            socket.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());

            // Some Json magic stuff
            JSONObject credentials = new JSONObject() {
                {
                    this.put("email", email);
                    this.put("password", password);
                }
            };

            writer.write(credentials.toString());
            writer.flush();
            writer.close();

            System.out.println("Getting Response of " + socket.getResponseCode());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String data = reader.readLine().trim();

            JSONObject dataJSON = new JSONObject(data);

            if (dataJSON.has("error")) {
                return new Session("", dataJSON.getString("error"));
            }

            return new Session(email, dataJSON.getString("token"), dataJSON.getJSONObject("user"));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("lol something went wrong");
            return null;
        }
    }

}