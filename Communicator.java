import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
import java.util.*;

public class Communicator {

    public static Session login(String username, String password) {
        try {

            // Init connection
            HttpsURLConnection socket = (HttpsURLConnection) new URL("https://authentication.rastera.xyz").openConnection();

            // Header stuff
            socket.setRequestMethod("POST");
            socket.setRequestProperty("User-Agent", "Mozilla/5.0");
            socket.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            socket.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            socket.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());

            /*
            JSONObject credentials = new JSONObject() {
                {
                    put("username", username);
                    put("password", password);
                }
            };

            writer.write(credentials.toString());*/
            String credentials = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

            System.out.println(credentials);

            writer.write(credentials);
            writer.flush();
            writer.close();

            System.out.println("Getting Response of " + socket.getResponseCode());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line+"\n");

            }
            reader.close();

            System.out.println(sb.toString());

            return null;

        } catch (Exception e) {
            // Something went wrong :'(
            //e.printStackTrace();
            return null;
        }
    }
}