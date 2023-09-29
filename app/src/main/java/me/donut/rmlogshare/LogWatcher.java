package me.donut.rmlogshare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.BasicFileAttributes;
import static me.donut.rmlogshare.Logger.*;

/**
 * Watches the log file for changes
 */
public class LogWatcher extends Thread {

	Path path;
	int lastFileLines = -1;
	FileTime lastUpdateTime;
	FileTime lastCreateTime;
	boolean stop = false;
	private final String OS = System.getProperty("os.name").toLowerCase();
    private final boolean IS_WINDOWS = (OS.indexOf("win") >= 0);
    private final boolean IS_MAC = (OS.indexOf("mac") >= 0);
    private final boolean IS_UNIX = (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

	public LogWatcher(String filePath) {
		if (filePath.equals("")) return;
		path = Paths.get(filePath);
	}

	/**
	 * Update the path to fit the client used
	 * @param userClient The client to be used
	 */
	public void changeClient(RmLogShare.Client userClient) {
		if (path == null) {
			String minecraftDir = "null";
			if (IS_WINDOWS) minecraftDir = String.join(File.separator, new String[] {
						System.getProperty("user.home"), "AppData", "Roaming", ".minecraft"});
			else if (IS_MAC) minecraftDir = String.join(File.separator, new String[] {
						System.getProperty("user.home"), "Library", "Application Support", "minecraft"});
			else if (IS_UNIX) minecraftDir = String.join(File.separator, new String[] {
						System.getProperty("user.home"), ".minecraft"});

			switch (userClient) {
				case LABYMOD: case VANILLA:
					path = Paths.get(String.join(File.separator, new String[] {
						minecraftDir, "logs", "latest.log"
					}));
					break;
				case BADLION:
					path = Paths.get(String.join(File.separator, new String[] {
						minecraftDir, "logs", "blclient", "minecraft", "latest.log"
					}));
					break;
				case LUNAR:
					path = Paths.get(String.join(File.separator, new String[] {
						System.getProperty("user.home"),
						".lunarclient", "offline", "multiver", "logs", "latest.log"
					}));
					break;
			}
		}

		info("Beobachte Datei '" + path.toString() + "'");
		RmLogShare.getInstance().getLogWatcher().start();
	}
	
	public void run() {
		try {
			while(!stop) {
				BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
				FileTime updateTime = attr.lastModifiedTime();
				FileTime createTime = attr.creationTime();

				if (lastCreateTime == null || createTime.compareTo(lastCreateTime) != 0) {
					lastCreateTime = createTime;
					lastFileLines = -1;
				}

				if (lastUpdateTime == null || updateTime.compareTo(lastUpdateTime) != 0) {
					lastUpdateTime = updateTime;
					readChanges();
				}

				Thread.sleep(100);
			}
		} catch (NoSuchFileException ex) {
			error("\nDatei nicht gefunden '" + path.toString() + "'");
			System.exit(1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Searches the file for new content
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void readChanges() throws IOException, FileNotFoundException {
		BufferedReader in = new BufferedReader(new FileReader(path.toAbsolutePath().toString()));
		String line;
		int lineCount = 0;
		while ((line = in.readLine()) != null) {
			if (lineCount++ < lastFileLines || lastFileLines == -1) continue;
			line = line.substring(Math.min(11, line.length()));

			// if (RmLogShare.getInstance().getUserPrompt().isAdminMode() && 
			// 		line.startsWith("[Client thread/INFO]: [CHAT] Teams sind auf diesem Server VERBOTEN"))
			// 	RmLogShare.getInstance().getSocketHandler().admin("start");

			if (line.toLowerCase().endsWith(": go!") || line.toLowerCase().endsWith(": go! ?")) {
				String senderName = line.substring(29, line.length() - (line.toLowerCase().endsWith(": go!") ? 5 : 7));
				if (senderName.startsWith("[")) senderName = senderName.split(" ")[1];
				RmLogShare.getInstance().getSocketHandler().sendMessage("PAR " + senderName, true);
				continue;
			}

			if (!line.startsWith("[Client thread/INFO]: [CHAT] [RageMode] ")) continue;		
			line = line.substring(Math.min(40, line.length()));

			if (RmLogShare.getInstance().getUserPrompt().isTestMode()) {
				info("**TEST** " + line);
			} else {
				RmLogShare.getInstance().getSocketHandler().sendMessage("DTA " + line, true);
			}
		}
		lastFileLines = lineCount;
		in.close();
	}

	/**
	 * Stops the file watcher
	 */
	public void terminate() {
		stop = true;
	}
}
