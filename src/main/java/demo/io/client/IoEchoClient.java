package demo.io.client;

import demo.common.Demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class IoEchoClient extends Demo {

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] msgs = {"Alpha", "Bravo", "Charlie"};
        for (String msg : msgs) {
            Socket socket = new Socket("localhost", 7000);
            System.out.println("echo client started: " + socket);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            byte[] bytes = msg.getBytes();
            os.write(bytes);

            int totalRead = 0;
            int read;
            while (totalRead < bytes.length) {
                if ((read = is.read(bytes, totalRead, bytes.length - totalRead)) == -1)
                    throw new SocketException("Connection closed prematurely");
                System.out.println("client read: " + read);
                totalRead += read;
            }

            System.out.println("echo client received: " + new String(bytes));

            socket.close();
            System.out.println("echo client disconnected");
            Thread.sleep(1000);
        }
    }
}