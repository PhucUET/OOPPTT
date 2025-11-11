package unknown.oopptt.net.server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameServer {
    private static final int PORT = 5000;
    private static final int MAX_PLAYERS = 4;
    private static final int DISCOVERY_PORT = 8888;

    private ServerSocket serverSocket;
    private final List<PlayerHandler> players = new CopyOnWriteArrayList<>();
    private boolean running = true;

    // Cờ điều khiển broadcast
    private volatile boolean discoveryRunning = true;

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            // Bắt đầu broadcast discovery trong thread riêng
            new Thread(this::startDiscovery, "DiscoveryThread").start();

            // Nhận kết nối người chơi
            while (running) {
                if (players.size() < MAX_PLAYERS) {
                    Socket socket = serverSocket.accept();
                    PlayerHandler player = new PlayerHandler(socket, this);
                    players.add(player);
                    player.start();
                    System.out.println("[SERVER] New player connected. Total: " + players.size());

                    // Nếu đã đủ người → dừng discovery
                    if (players.size() >= MAX_PLAYERS && discoveryRunning) {
                        System.out.println("[DISCOVERY] Room full → stopping broadcast.");
                        discoveryRunning = false;
                    }

                } else {
                    System.out.println("[SERVER] Room full. Rejecting new connection.");
                    Socket s = serverSocket.accept();
                    s.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    // 🔸 Broadcast IP server qua UDP cho client LAN
    private void startDiscovery() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            InetAddress broadcast = InetAddress.getByName("255.255.255.255");
            String ip = InetAddress.getLocalHost().getHostAddress();
            String msg = "SERVER_IP:" + ip + ":" + PORT;
            byte[] data = msg.getBytes();

            System.out.println("[DISCOVERY] Broadcasting server IP: " + ip);

            while (running && discoveryRunning) {
                DatagramPacket packet = new DatagramPacket(data, data.length, broadcast, DISCOVERY_PORT);
                socket.send(packet);
                Thread.sleep(3000); // gửi mỗi 3 giây
            }

            System.out.println("[DISCOVERY] Broadcast thread ended.");
        } catch (Exception e) {
            System.out.println("[DISCOVERY] Broadcast stopped: " + e.getMessage());
        }
    }

    // Gửi tin nhắn đến tất cả người chơi
    public synchronized void broadcast(String message, PlayerHandler sender) {
        for (PlayerHandler p : players) {
            if (p != sender) p.sendMessage(message);
        }
    }

    // Xoá player khi thoát
    public synchronized void removePlayer(PlayerHandler player) {
        players.remove(player);
        System.out.println("[SERVER] Player disconnected. Total: " + players.size());

        // Nếu có người thoát → mở lại discovery để cho phép người mới vào
        if (players.size() < MAX_PLAYERS && !discoveryRunning) {
            System.out.println("[DISCOVERY] Re-opening broadcast (slot available).");
            discoveryRunning = true;
            new Thread(this::startDiscovery, "DiscoveryThread").start();
        }
    }

    public void stop() {
        running = false;
        discoveryRunning = false;
        try {
            for (PlayerHandler p : players) {
                p.close();
            }
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GameServer().start();
    }
}
