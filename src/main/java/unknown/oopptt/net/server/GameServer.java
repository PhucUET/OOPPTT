package unknown.oopptt.net.server;


import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameServer {
    private static final int PORT = 5000;
    private static final int MAX_PLAYERS = 4;

    private ServerSocket serverSocket;
    private final List<PlayerHandler> players = new CopyOnWriteArrayList<>();
    private final GameState gameState = new GameState();
    private boolean running = true;

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Game server started on port " + PORT);

            // Thread nhận kết nối người chơi
            while (running) {
                if (players.size() < MAX_PLAYERS) {
                    Socket socket = serverSocket.accept();
                    PlayerHandler player = new PlayerHandler(socket, this);
                    players.add(player);
                    player.start();
                    System.out.println("New player connected. Total: " + players.size());
                } else {
                    System.out.println("Room full. Rejecting new connection.");
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

    // Gửi trạng thái game tới tất cả client
    public synchronized void broadcast(String message, PlayerHandler player) {
        for (PlayerHandler p : players) {
            if(p != player) {
                p.sendMessage(message);
            }
        }
    }

    // Xoá player khi thoát
    public synchronized void removePlayer(PlayerHandler player) {
        players.remove(player);
        System.out.println("Player disconnected. Total: " + players.size());
    }

    // Cập nhật logic game (tạm thời mô phỏng)
    public void updateGame() {
        // Ở bước sau: cập nhật vị trí bóng, power-up, điểm số...
    }

    public GameState getGameState() {
        return gameState;
    }

    public void stop() {
        running = false;
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
