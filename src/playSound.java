import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * 
 * @improver wuwenjie
 * @date 20130722
 */
//http://edwin.baculsoft.com/2010/11/how-to-play-mp3-files-with-java/
//ffmpeg -i a.mp3 -ss 00:00:11 -t 00:00:01 -ar 40000 -ac 2 a.wav

public class playSound extends Thread {

	private String filename;

	public playSound(String filename) {
		super();
		this.filename = filename;
	}

	public void run() {
		try {
			File file = new File(filename);

			AudioInputStream in = AudioSystem.getAudioInputStream(file);
			AudioInputStream din = null;
			AudioFormat baseFormat = in.getFormat();
			AudioFormat decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			din = AudioSystem.getAudioInputStream(decodedFormat, in);

			// play it...
			rawplay(decodedFormat, din);
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void rawplay(AudioFormat targetFormat,
			AudioInputStream din) throws IOException, LineUnavailableException {
		byte[] data = new byte[4096];
		SourceDataLine line = getLine(targetFormat);
		if (line != null) {
			// Start
			line.start();
			int nBytesRead = 0, nBytesWritten = 0;
			while (nBytesRead != -1) {
				nBytesRead = din.read(data, 0, data.length);
				if (nBytesRead != -1) {
					nBytesWritten = line.write(data, 0, nBytesRead);
				}

			}
			// Stop
			line.drain();
			line.stop();
			line.close();
			din.close();
		}

	}

	private synchronized SourceDataLine getLine(AudioFormat audioFormat)
			throws LineUnavailableException {
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);

		return res;
	}

	// my relative path file name
	// String song = "Bondan ft. Fade2Black-Ya Sudahlah.mp3";

	// playSound mp3Sound = new playSound(song);
	// mp3Sound.start();

}