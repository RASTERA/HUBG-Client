package com.rastera.Networking;

import com.badlogic.gdx.Gdx;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Util.Rah;

import com.rastera.Networking.Message;
import com.rastera.hubg.desktop.Main;
import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class Communicator {

    private Socket serverSock;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private LinkedBlockingQueue<Message> message;
    private HUBGGame client;
    private String serverName;
    private boolean listening = true;

    private Thread receiver;

    public Communicator(String ip, int port, final HUBGGame client) throws Exception {
        this.client = client;

        System.out.println("Connecting...");

        //this.serverSock.setSoTimeout(1000);
        this.serverSock = new Socket(InetAddress.getByAddress(new byte[] {127,0,0,1}), 8080);

        this.out = new ObjectOutputStream(serverSock.getOutputStream());
        this.in = new ObjectInputStream(serverSock.getInputStream());

        System.out.println("Connected to server: " + ip + ":" + port);

        receiver = new Thread(){
            public void run() {
                while (listening) {
                    try {
                        Message msg = (Message) in.readObject();

                        client.CommandProcessor(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                        //Main.errorQuit("Disconnected from server");
                    }
                }
            }
        };

        receiver.setDaemon(true);
        receiver.start();

        // Request server name
        this.write(-1, null);

    }

    public boolean isEmpty() {
        return message.isEmpty();
    }

    public Message read() {
        try {
            return message.take();
        } catch (Exception e) {
            return null;
        }
    }

    public void write(int type, Object Message) {
        try {
            this.out.writeObject(Rah.messageBuilder(type, Message));
        } catch (Exception e) {
            e.printStackTrace();
            Main.errorQuit("Disconnected from server");
        }
    }
}