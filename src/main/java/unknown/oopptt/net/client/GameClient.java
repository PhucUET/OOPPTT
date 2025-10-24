package unknown.oopptt.net.client;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class GameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread listener;
    private boolean connected = false;

    public GameClient(String ip, int port, String playerName, Consumer<String> callback) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;
            out.println(playerName);

            listener = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        callback.accept(msg);
                    }
                } catch (IOException e) {
                    System.out.println("⚠️ Connection lost.");
                }
            });
            listener.setDaemon(true);
            listener.start();
        } catch (IOException e) {
            System.out.println("❌ Cannot connect: " + e.getMessage());
        }
    }

    public void send(String message) {
        if (connected) out.println(message);
    }

    public void close() {
        try {
            connected = false;
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
