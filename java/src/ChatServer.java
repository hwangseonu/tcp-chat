import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ChatServer {

    private static final int PORT = 7007;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);

            while (true) {
                System.out.println("waiting...");
                Socket socket = server.accept();
                ChatThread thread = new ChatThread(socket);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class ChatThread extends Thread {
    private final Socket socket;
    private final String id;
    private final BufferedReader reader;

    public static final HashMap<String, PrintWriter> socketMap = new HashMap<>();


    public ChatThread(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.id = reader.readLine() + "(" + socket.getInetAddress().getHostAddress() + ")";

        this.broadcast(id + " enter the chatroom");

        synchronized (socketMap) {
            socketMap.put(this.id, new PrintWriter(new OutputStreamWriter(socket.getOutputStream())));
        }
    }

    @Override
    public void run() {
        try {
            String line;

            while ((line = this.reader.readLine()) != null) {
                broadcast(this.id + ": " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (socketMap) {
                socketMap.remove(this.id);
            }
            broadcast(this.id + " quit the chat");
            try {
                if (this.socket != null) {
                    this.socket.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void broadcast(String message) {
        System.out.println(message);
        synchronized (socketMap) {
            for (PrintWriter writer : socketMap.values()) {
                writer.println(message);
                writer.flush();
            }
        }
    }
}

