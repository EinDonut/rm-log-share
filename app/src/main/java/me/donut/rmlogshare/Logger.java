package me.donut.rmlogshare;

import java.io.IOException;

/**
 * Prints formatted messages
 */
public class Logger {
	
	/**
	 * Prints an info message
	 * @param msg The content of the message
	 */
	public static void info(String msg) {
		System.out.println("[INFO] " + msg);
	} 

	/**
	 * Prints an error message
	 * @param msg The content of the message
	 */
	public static void error(String msg) {
		System.out.println("[ERROR] " + msg);
	}

	/**
	 * Tells the user to do some input, no line break
	 * @param msg The message
	 */
	public static void action(String msg) {
		System.out.print("[!] " + msg);
	}

	/**
	 * Clears the cli
	 */
	public static void clearScreen() {
		try {
			if (System.getProperty("os.name").contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				Runtime.getRuntime().exec("clear");
			}
		} catch (IOException | InterruptedException ex) { }
	}
}
