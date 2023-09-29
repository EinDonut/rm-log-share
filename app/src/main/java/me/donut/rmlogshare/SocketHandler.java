package me.donut.rmlogshare;

import java.net.Socket;
import java.net.SocketException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import static me.donut.rmlogshare.Logger.*;

/**
 * Handles the communication with the server
 */
public class SocketHandler extends Thread {
	
	private int port;
	private String ip;
	private PrintWriter out;
	private BufferedReader in;
	private Socket clientSocket;

	public SocketHandler(String ip, int port) {
		this.ip = ip;
		this.port = port;

		if (!ip.equals("")) return;
		error("Es wurde keien IP angegeben!");
		System.exit(1);
	}

	public void run() {
		connect();
	}

	/**
	 * Sends a message to the server
	 * @param msg The content of the message
	 * @param keepAlive Weather to check for a keepalive response, set to false to handle response afterwards
	 */
	public void sendMessage(String msg, boolean keepAlive) {
		try {
			out.println(msg);

			if (!keepAlive) return;
			clientSocket.setSoTimeout(5000);
			String response = in.readLine();
			if (!(response.equals("ACK"))) {
				throw new SocketException();
			}
			clientSocket.setSoTimeout(0);
		} catch (Exception ex) {
			ex.printStackTrace();
			error("Verbindung verloren");
			stopConnection();
			connect();
		}
    }

	/**
	 * Closes the connection to the server
	 */
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

	/**
	 * Attempts to connect to the server
	 */
	private void connect() {
		try {
			clientSocket = new Socket(ip, port);
			clientSocket.setKeepAlive(true);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			info("Verbindung erfolgreich");
			RmLogShare.getInstance().getUserPrompt().promptClient();
		} catch (Exception ex) {
			info("Verbindung fehlgeschlagen");
			reconnect();
		}
	}

	/**
	 * Starts a 5 second countdown and try to connect again
	 */
	private void reconnect() {
		try {
			error("Erneuter Verbindungsversuch in 5 Sekunden...");
			Thread.sleep(5000);
			connect();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Attempts to authenticate the user
	 * @param userID The unique user identifier, has to be entered manually
	 */
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

	/**
	 * Passes an admin command to the server
	 * @param cmd The command to send
	 */
	public void admin(String cmd) {
		cmd = cmd.toLowerCase();
		sendMessage("ADM " + cmd, false);
		if (cmd.startsWith("export") || cmd.startsWith("exportlog")) {
			receiveFile();
			return;
		}

		try {
			String response = in.readLine();
			info("Antwort: \n" + response);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void receiveFile() {
		try {
			String filename = in.readLine();
			if (!filename.contains(".")) {
				error("Fehler bei der Übermittlung der Datei");
				info(filename);
				return;
			}
			String content = in.readLine().replace("$", System.lineSeparator());

			File file;
			int c = 0;
			String[] split = filename.split("\\.");
			while((file = new File(filename)).exists())
				filename = split[0] + "_" + c++ + "." + split[1];
			
			System.out.println(filename);
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.close();
			info("Datei '" + filename + "' heruntergeladen");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
