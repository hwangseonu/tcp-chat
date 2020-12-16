import java.io.*;
import java.net.Socket;

public class ChatClient {

    public static final int PORT = 7007;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("args: <server ip>");
            System.exit(-1);
        }

        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;

        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Username: ");
            String id = input.readLine();

            socket = new Socket(args[0], PORT);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.println(id);
            writer.flush();

            PrintThread thread = new PrintThread(socket, reader);
            thread.start();

            String line;
            do {
                System.out.print("> ");
                line = input.readLine();
                if (line == null) break;
                writer.println(line);
                writer.flush();
            } while (true);
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
                System.out.println("\r" + line);
                System.out.print("> ");
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
