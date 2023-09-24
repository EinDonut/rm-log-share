package me.donut.rmlogshare;

import java.io.IOException;

public class Logger {
	
	public static void info(String msg) {
		System.out.println("[INFO] " + msg);
	} 

	public static void error(String msg) {
		System.out.println("[ERROR] " + msg);
	}

	public static void action(String msg) {
		System.out.print("[!] " + msg);
	}

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
