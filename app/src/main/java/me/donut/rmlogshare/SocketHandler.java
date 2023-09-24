package me.donut.rmlogshare;

import java.net.Socket;
import java.net.SocketException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import static me.donut.rmlogshare.Logger.*;

public class SocketHandler extends Thread {
	
	private int port;
	private String ip;
	private PrintWriter out;
	private BufferedReader in;
	private Socket clientSocket;

	public SocketHandler(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void run() {
		connect();
	}

	public void sendMessage(String msg, boolean keepAlive) {
		try {
			out.println(msg);

			if (!keepAlive) return;
			clientSocket.setSoTimeout(3000);
			if (!(in.readLine().equals("KEA"))) throw new SocketException();
			clientSocket.setSoTimeout(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			error("Verbindung verloren");
			stopConnection();
			connect();
		}
    }

    public void stopConnection() {
		if (clientSocket == null) return;
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
			info("Verbindung erfolgreich");
			RmLogShare.getInstance().getUserPrompt().promptLogin();
		} catch (Exception ex) {
			info("Verbindung fehlgeschlagen");
			reconnect();
		}
	}

	private void reconnect() {
		try {
			error("Erneuter Verbindungsversuch in 5 Sekunden...");
			Thread.sleep(5000);
			connect();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public boolean isConnected() {
		return clientSocket != null && !clientSocket.isClosed();
	}

	public void login(String userID) {
		sendMessage("LGN " + userID, false);
		clearScreen();
		try {
			String response = in.readLine();
			if (response.startsWith("OK ") && response.length() > 3) {
				info("Eingeloggt als '" + response.split(" ")[1] + "'");
				RmLogShare.getInstance().getUserPrompt().setLoggedIn(true);
				if (!response.endsWith("admin")) return;
				info("Starte Admin-Konsole");
				RmLogShare.getInstance().getUserPrompt().startAdminPrompt();
			} else {
				info("Ein Fehler ist aufgetreten, bitte prüfe deine Kennnummer");
				RmLogShare.getInstance().getUserPrompt().promptLogin();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void admin(String cmd) {
		sendMessage("ADM " + cmd, false);
		try {
			String response = in.readLine();
			info("Antwort: \n" + response);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
