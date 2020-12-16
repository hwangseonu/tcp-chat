import java.io.*;
import java.net.Socket;

public class ChatClient {

    public static final String HOST = "localhost";
    public static final int PORT = 7007;

    public static void main(String[] args) {
        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;

        try {
            System.out.print("Username: ");
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            String id = input.readLine();

            socket = new Socket(HOST, PORT);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.println(id);
            writer.flush();

            PrintThread thread = new PrintThread(socket, reader);
            thread.start();

            String line;
            while((line = input.readLine()) != null) {
                writer.println(line);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
                if (reader != null) reader.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

class PrintThread extends Thread {
    private final Socket socket;
    private final BufferedReader reader;


    public PrintThread(Socket socket, BufferedReader reader) {
        this.socket = socket;
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            String line;
            while((line = this.reader.readLine()) != null) {
                System.out.println(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (this.reader != null) this.reader.close();
                if (this.socket != null) this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
