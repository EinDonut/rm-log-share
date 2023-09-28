package me.donut.rmlogshare;

public class RmLogShare {

	private SocketHandler socketHandler;
	private LogWatcher logWatcher;
	private UserPrompt userPrompt;
	private static RmLogShare instance;
	
	public RmLogShare(String watchPath) {
		instance = this;
		socketHandler = new SocketHandler("192.168.2.129", 25566);

		logWatcher = new LogWatcher(watchPath);
		userPrompt = new UserPrompt();

		socketHandler.start();

		registerShutdownHook();
	}
	
	/**
	 * @return The SocketHandler, which handles the communication with the server
	 */
	public SocketHandler getSocketHandler() {
		return socketHandler;
	}

	/**
	 * @return The LogWatcher, which detects changes in the log file
	 */
	public LogWatcher getLogWatcher() {
		return logWatcher;
	}

	/**
	 * @return The UserPrompt, which handles the user input
	 */
	public UserPrompt getUserPrompt() {
		return userPrompt;
	}

	public static RmLogShare getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		new RmLogShare(args.length > 0 ? args[0] : "");
	}

	private void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				getSocketHandler().stopConnection();
			}
		});
	}

	/**
	 * Represent the minecraft client type
	 */
	public enum Client {
		VANILLA,
		BADLION,
		LUNAR,
		LABYMOD
	}
}
