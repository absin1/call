package call;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import javax.sound.sampled.*;

public class Mic {
	public byte[] buffer;
	private int port;
	static AudioInputStream ais;

	public static void main(String[] args) {
		TargetDataLine line;
		DatagramPacket dgp;

		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float rate = 44100.0f;
		int channels = 2;
		int sampleSize = 16;
		boolean bigEndian = true;
		InetAddress addr;

		AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate,
				bigEndian);

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("Line matching " + info + " not supported.");
			return;
		}

		try {
			line = (TargetDataLine) AudioSystem.getLine(info);

			// TOTALLY missed this.
			int buffsize = line.getBufferSize() / 5;
			buffsize += 512;

			line.open(format);

			line.start();

			int numBytesRead;
			byte[] data = new byte[buffsize];

			/*
			 * MICK's injection: We have a buffsize of 512; it is best if the frequency
			 * evenly fits into this (avoid skips, bumps, and pops). Additionally, 44100 Hz,
			 * with two channels and two bytes per sample. That's four bytes; divide 512 by
			 * it, you have 128.
			 * 
			 * 128 samples, 44100 per second; that's a minimum of 344 samples, or 172 Hz.
			 * Well within hearing range; slight skip from the uneven division. Maybe bump
			 * it up to 689 Hz.
			 * 
			 * That's a sine wave of shorts, repeated twice for two channels, with a
			 * wavelength of 32 samples.
			 * 
			 * Note: Changed my mind, ignore specific numbers above.
			 * 
			 */
			{
				final int λ = 16;
				ByteBuffer buffer = ByteBuffer.allocate(λ * 2 * 8);
				for (int j = 0; j < 2; j++) {
					for (double i = 0.0; i < λ; i++) {
						System.out.println(j + " " + i);
						// once for each sample
						buffer.putShort((short) (Math.sin(Math.PI * (λ / i)) * Short.MAX_VALUE));
						buffer.putShort((short) (Math.sin(Math.PI * (λ / i)) * Short.MAX_VALUE));
					}
				}

				data = buffer.array();
			}

			addr = InetAddress.getByName("127.0.0.1");
			try (DatagramSocket socket = new DatagramSocket()) {
				while (true) {
					for (byte b : data)
						System.out.print(b + " ");

					// Read the next chunk of data from the TargetDataLine.
					// numBytesRead = line.read(data, 0, data.length);

					for (int i = 0; i < 64; i++) {
						byte b = data[i];
						System.out.print(b + " ");
					}
					System.out.println();

					// Save this chunk of data.
					dgp = new DatagramPacket(data, data.length, addr, 50005);

					for (int i = 0; i < 64; i++) {
						byte b = dgp.getData()[i];
						System.out.print(b + " ");
					}
					System.out.println();

					socket.send(dgp);
				}
			}

		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO: handle exception
		} catch (SocketException e) {
			// TODO: handle exception
		} catch (IOException e2) {
			// TODO: handle exception
		}
	}
}