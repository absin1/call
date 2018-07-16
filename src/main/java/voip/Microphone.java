package voip;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * This class is used to get input from the the users Microphone.
 *
 * @author Suraj Kumar <k975@live.co.uk>
 * @version 1.0
 */
public class Microphone {
	/**
	 * The sample rate.
	 */
	public static final float SAMPLE_RATE = 8000.0f;
	/**
	 * The sample size.
	 */
	public static final int SAMPLE_SIZE = 16;

	/**
	 * The TargetDataLine used to capture input from the Microphone.
	 */
	private TargetDataLine line;

	/**
	 * AudioFormat instance used to interpret the sound data.
	 */
	private AudioFormat format;

	/**
	 * Constructs a new Microphone.
	 */
	public Microphone() {
		format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, false);
	}

	/**
	 * Opens the users microphone.
	 * 
	 * @return If the line was opened successfully.
	 */
	public boolean open() {
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		boolean lineOpen = false;

		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			lineOpen = true;
		} catch (LineUnavailableException e) {
			lineOpen = false;
			System.err.println("There was an error open your microphone:" + e.getMessage());
		}
		return lineOpen;
	}

	/**
	 * Starts the {@link Microphone#TargetDataLine}.
	 */
	public void start() {
		line.start();
	}

	/**
	 * Reads the current byte read from {@link Microphone#TargetDataLine}.
	 */
	public int read(byte[] buf, int off, int len) {
		return line.read(buf, off, len);
	}

	/**
	 * Closes {@link Microphone#TargetDataLine}.
	 */
	public void stop() {
		line.stop();
		line.flush();
	}

	/**
	 * @return The {@link Microphone#TargetDataLine} buffer size.
	 */
	public int getBufferSize() {
		return line.getBufferSize();
	}
}