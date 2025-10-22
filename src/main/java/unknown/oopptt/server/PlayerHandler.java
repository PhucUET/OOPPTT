package unknown.oopptt.server;

import java.io.*;
import java.net.*;

public class PlayerHandler extends Thread {
    private final Socket socket;
    private final GameServer server;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;

    public PlayerHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Nhận tên người chơi
            out.println("WELCOME! Enter your player name:");
            playerName = in.readLine();
            System.out.println(playerName + " joined the game.");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                //System.out.println(playerName + ": " + inputLine);
                server.broadcast(inputLine,this);
            }

        } catch (IOException e) {
            System.out.println("Connection lost with player.");
        } finally {
            close();
            server.removePlayer(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}