package voip;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * This class is used play back audio.
 *
 * @author Suraj Kumar <k975@live.co.uk>
 * @version 1.0
 */
public class Speaker {
	/**
	 * The sample rate.
	 */
	public static final float SAMPLE_RATE = 8000.0f;
	/**
	 * The sample size.
	 */
	public static final int SAMPLE_SIZE = 16;

	/**
	 * The SourceDataLine used for playback to the users speaker.
	 */
	private SourceDataLine line;

	/**
	 * AudioFormat instance used to interpret the sound format.
	 */
	private AudioFormat format;

	/**
	 * Constructs a new {@link Speaker}.
	 */
	public Speaker() {
		format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, 1, true, false);
	}

	/**
	 * Opens the users speakers.
	 * 
	 * @return If the line was opened successfully.
	 */
	public boolean open() {
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		boolean lineOpen = false;
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format);
			lineOpen = true;
		} catch (LineUnavailableException e) {
			lineOpen = false;
			System.err.println("There was an error loading your speakers:" + e.getMessage());
		}
		return lineOpen;
	}

	/**
	 * Starts the {@link Speaker#SourceDataLine}.
	 */
	public void start() {
		line.start();
	}

	/**
	 * Sends sound to the speakers
	 * 
	 * @param buf
	 *            The sound to play
	 * @param off
	 *            The current offset of the buffer
	 * @param len
	 *            The length of the sound
	 */
	public void write(byte[] buf, int off, int len) {
		line.write(buf, off, len);
	}

	/**
	 * Closes @link Speaker#SourceDataLine}.
	 */
	public void stop() {
		line.stop();
		line.flush();
	}

	/**
	 * @return The @link Speaker#SourceDataLine} buffer size.
	 */
	public int getBufferSize() {
		return line.getBufferSize();
	}
}