package me.donut.rmlogshare.connection;

import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SocketHandler extends Thread {
	
	private int port;
	private String ip;
	private PrintWriter out;
	private BufferedReader in;
	private Socket clientSocket;

	public SocketHandler(String ip, int port) {
		this.ip = ip;
		this.port = port;

		start();
	}

	public void run() {
		connect();
	}

	public void sendMessage(String msg) {
		if (clientSocket == null || !clientSocket.isConnected()) {
			reconnect();
			return;
		}

		try {
			out.println(msg);
			System.out.println("> " + msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }

    public void stopConnection() {
		try {
			in.close();
			out.close();
			clientSocket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }

	private void connect() {
		try {
			clientSocket = new Socket(ip, port);
			clientSocket.setKeepAlive(true);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (Exception ex) {
			reconnect();
		}
	}

	private void reconnect() {
		try {
			System.out.println("Connection failed, retrying in 5 seconds...");
			Thread.sleep(5000);
			connect();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}
