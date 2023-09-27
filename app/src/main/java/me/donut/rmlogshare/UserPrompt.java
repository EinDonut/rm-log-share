package me.donut.rmlogshare;

import java.util.NoSuchElementException;
import java.util.Scanner;
import static me.donut.rmlogshare.Logger.*;

/**
 * Handles user input
 */
public class UserPrompt {

	private Scanner scanner;
	private boolean testMode = false;
	private boolean adminMode = false;
	private boolean loggedIn = false;
	private RmLogShare.Client userClient;

	public UserPrompt() {
		scanner = new Scanner(System.in);
	}

	/**
	 * Determines the minecraft client used
	 */
	public void promptClient() {
		action("Bitte gibt den Namen deines Clients ein (VANILLA, LABYMOD, BADLION, LUNAR): ");
		try {
			String clientString = scanner.nextLine().toUpperCase();
			if (clientString.equals("")) clientString = "VANILLA";
			userClient = RmLogShare.Client.valueOf(clientString);
		} catch (NoSuchElementException ex) {
			return;
		} catch (IllegalArgumentException ex) {
			error("Unbekannter Client. Wenn dein Client nicht in der Liste steht, gib VANILLA an und hoffe auf das Beste!");
			promptClient();
			return;
		}
		RmLogShare.getInstance().getLogWatcher().changeClient(getUserClient());
		promptLogin();
	}

	/**
	 * Asks for the user id and enters testmode if asked to 
	 */
	public void promptLogin() {
		action("Bitte gib deine Kennnummer ein: ");

		String userID;
		try {
			userID = scanner.nextLine();
		} catch (Exception ex) {
			return;
		}

		if (userID.equalsIgnoreCase("test")) {
			testMode = true;
			info("Test-Modus gestartet. Es werden keine Chatnachrichten übermittelt");
			return;
		}

		RmLogShare.getInstance().getSocketHandler().login(userID);
	}

	/**
	 * Waits for admin commands to pass along
	 */
	public void startAdminPrompt() {
		try {
			adminMode = true;
			action("Befehl eingeben: ");
			RmLogShare.getInstance().getSocketHandler().admin(scanner.nextLine());
			startAdminPrompt();
		} catch (NoSuchElementException ex) {	}
	}

	/**
	 * Weather the user was authenticated 
	 * @return true if authenticated
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * Updates the authentication status of the user
	 * @param loggedIn new status
	 */
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	/**
	 * Weather the application is running in test mode
	 * @return true if in test mode
	 */
	public boolean isTestMode() {
		return testMode;
	}

	/**
	 * Weather the application is running in admin mode
	 * @return true if in admin mode
	 */
	public boolean isAdminMode() {
		return adminMode;
	}

	/**
	 * @return The client used
	 */
	public RmLogShare.Client getUserClient() {
		return userClient;
	}
}
