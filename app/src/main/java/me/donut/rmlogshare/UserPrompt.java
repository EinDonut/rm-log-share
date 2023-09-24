package me.donut.rmlogshare;

import java.util.Scanner;
import static me.donut.rmlogshare.Logger.*;

public class UserPrompt {

	private Scanner scanner;
	private boolean testMode = false;
	private boolean loggedIn = false;

	public UserPrompt() {
		scanner = new Scanner(System.in);
	}

	public void promptLogin() {
		action("Bitte gib deine Kennnummer ein: ");
		String userID = scanner.nextLine();

		if (userID.equalsIgnoreCase("test")) {
			testMode = true;
			info("Test-Modus gestartet. Es werden keine Chatnachrichten übermittelt");
			return;
		}

		RmLogShare.getInstance().getSocketHandler().login(userID);
	}

	public void startAdminPrompt() {
		RmLogShare.getInstance().getLogWatcher().terminate();
		action("Befehl eingeben: ");
		RmLogShare.getInstance().getSocketHandler().admin(scanner.nextLine());
		startAdminPrompt();
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public boolean isTestMode() {
		return testMode;
	}
}
