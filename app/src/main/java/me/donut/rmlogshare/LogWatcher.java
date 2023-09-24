package me.donut.rmlogshare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.BasicFileAttributes;

public class LogWatcher extends Thread {

	Path path;
	int lastFileLines = -1;
	FileTime lastUpdateTime;
	FileTime lastCreateTime;

	public LogWatcher() {
		path = Paths.get(String.join(File.separator, new String[] {
			System.getProperty("user.home"),
			"AppData", "Roaming", ".minecraft", "logs", "latest.log"
		}));
	}

	public void run() {
		try {
			while(true) {
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void readChanges() throws IOException, FileNotFoundException {
		BufferedReader in = new BufferedReader(new FileReader(path.toAbsolutePath().toString()));
		String line;
		int lineCount = 0;
		if (lastFileLines == -1) return;
		while ((line = in.readLine()) != null) {
			if (lineCount++ < lastFileLines) continue;
			line = line.substring(Math.min(11, line.length()));
			if (!line.startsWith("[Client thread/INFO]: [CHAT] ")) continue;
			RmLogShare.getInstance().getSocketHandler().sendMessage(line);
		}
		lastFileLines = lineCount;
	}
}
