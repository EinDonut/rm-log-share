package me.donut.rmlogshare;
import me.donut.rmlogshare.connection.SocketHandler;

public class RmLogShare {

	private SocketHandler socketHandler;
	private LogWatcher logWatcher;
	private static RmLogShare instance;
	
	public RmLogShare() {
		instance = this;
		socketHandler = new SocketHandler("127.0.0.1", 4001);
		logWatcher = new LogWatcher();
	}
	
	public SocketHandler getSocketHandler() {
		return socketHandler;
	}

	public LogWatcher getLogWatcher() {
		return logWatcher;
	}

	public static RmLogShare getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		new RmLogShare();
	}		
}
