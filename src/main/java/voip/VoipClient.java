package voip;

import java.io.IOException;
import java.net.Socket;

/**
 * The VoipClient used to start all needed servers
 * 
 * @author Suraj Kumar <k975@live.co.uk>
 * @version 1.0
 */
public class VoipClient implements Runnable {
	/**
	 * The host we want to connect to.
	 */
	private static final String HOST = System.getProperty("host", "localhost");
	/**
	 * The port to connect to the host with.
	 */
	private static final int PORT = Integer.parseInt(System.getProperty("port", "5060"));
	/**
	 * The Socket instance for server/client interaction.
	 */
	private Socket socket;
	/**
	 * The {@link Microphone} instance.
	 */
	private Microphone microphone;
	/**
	 * The {@link Speaker} instance.
	 */
	private Speaker speaker;

	/**
	 * Constructs a new {@link VoipClient}.
	 */
	public VoipClient() {
		this.microphone = new Microphone();
		this.speaker = new Speaker();
	}

	/**
	 * Connects to the server and handles threads for Input/Output.
	 */
	@Override
	public void run() {
		try {
			socket = new Socket(HOST, PORT);
			socket.setKeepAlive(true);
		} catch (Exception e) {
			System.err.println("Could not connect to " + HOST + "/" + PORT + ":" + e.getMessage());
			return;
		}

		// Reads data received from server
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (speaker.open()) {
					speaker.start();

					while (socket.isConnected()) {
						try {
							byte[] buffer = new byte[speaker.getBufferSize() / 5];
							int read = socket.getInputStream().read(buffer, 0, buffer.length);
							speaker.write(buffer, 0, read);
						} catch (IOException e) {
							System.err.println("Could not read data from server:" + e.getMessage());
						}
					}
				}
			}
		}).start();

		// Sends data to server
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (microphone.open()) {
					microphone.start();

					while (socket.isConnected()) {
						try {
							byte[] buffer = new byte[microphone.getBufferSize() / 5];
							int read = microphone.read(buffer, 0, buffer.length);
							socket.getOutputStream().write(buffer, 0, read);
						} catch (Exception e) {
							System.err.println("Could not send data to server:" + e.getMessage());
						}
					}
				}
			}
		}).start();
	}

	/**
	 * Launches the {@link VoipClient}.
	 * 
	 * @param args
	 *            Runtime arguments
	 */
	public static void main(String[] args) {
		VoipClient client = new VoipClient();
		new Thread(client).start();
	}
}