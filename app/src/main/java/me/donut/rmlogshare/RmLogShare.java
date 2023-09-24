package me.donut.rmlogshare;

public class RmLogShare {

	private SocketHandler socketHandler;
	private LogWatcher logWatcher;
	private UserPrompt userPrompt;
	private static RmLogShare instance;
	private boolean testMode = false;
	
	public RmLogShare() {
		instance = this;
		socketHandler = new SocketHandler("192.168.179.202", 25566);

		logWatcher = new LogWatcher();
		userPrompt = new UserPrompt();

		socketHandler.start();
		logWatcher.start();

		registerShutdownHook();
	}
	
	public SocketHandler getSocketHandler() {
		return socketHandler;
	}

	public LogWatcher getLogWatcher() {
		return logWatcher;
	}

	public UserPrompt getUserPrompt() {
		return userPrompt;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	public boolean isTestMode() {
		return testMode;
	}

	public static RmLogShare getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		new RmLogShare();
	}

	private void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				getSocketHandler().stopConnection();
			}
		});
	}
}
