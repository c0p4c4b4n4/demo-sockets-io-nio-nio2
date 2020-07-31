package tdp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	public static void main(String[] args) throws IOException {
		try (Socket socket = new Socket("localhost", 9001);) {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.print("Hello!");
			out.flush();
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String reply = in.readLine();
			System.out.println("Server responded: " + reply);
		}
	}
}
