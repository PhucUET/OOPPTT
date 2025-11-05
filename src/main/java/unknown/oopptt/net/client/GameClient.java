package unknown.oopptt.net.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.function.Consumer;

public class GameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread listener;
    private boolean connected = false;

    private static final int DISCOVERY_PORT = 8888;

    public GameClient(String ip, int port, String playerName, Consumer<String> callback) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;

            out.println(playerName);
            System.out.println("[CLIENT] Connected to server " + ip + ":" + port);

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

    // 🔸 Tự động tìm IP server qua UDP broadcast
    public static String findServerIP() {
        try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT, InetAddress.getByName("0.0.0.0"))) {
            socket.setSoTimeout(5000); // đợi tối đa 5s
            socket.setBroadcast(true);
            System.out.println("[CLIENT] Searching for server...");

            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String msg = new String(packet.getData(), 0, packet.getLength());

            if (msg.startsWith("SERVER_IP:")) {
                String[] parts = msg.split(":");
                String serverIP = parts[1];
                int port = Integer.parseInt(parts[2]);
                System.out.println("[CLIENT] Found server at " + serverIP + ":" + port);
                return serverIP;
            }
        } catch (SocketTimeoutException e) {
            System.out.println("[CLIENT] No server found in LAN.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
