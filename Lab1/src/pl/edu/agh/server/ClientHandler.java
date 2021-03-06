package pl.edu.agh.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static pl.edu.agh.server.Server.clientHandlerMap;

public class ClientHandler implements Runnable {

    private final Scanner scanner;
    private final PrintWriter printWriter;
    private Socket socket;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;

        scanner = new Scanner(socket.getInputStream());
        printWriter = new PrintWriter(socket.getOutputStream(), true);

    }

    @Override
    public void run() {
        String nick = scanner.nextLine();
        Server.registerUser(nick, this);
        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            sendToAll(nick, message);
        }
    }

    private void send(String message) {
        printWriter.write(message + '\n');
        printWriter.flush();
    }

    private void sendToAll(String nick, String message) {
        synchronized (clientHandlerMap) {
            clientHandlerMap.entrySet().stream()
                    .filter(a -> !a.getKey().equals(nick))
                    .forEach(a -> a.getValue().send(nick + ":" + message));
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
