package sound;

import javax.sound.sampled.*;
import java.io.InputStream;

public class SoundProducer extends Thread {
    private final SourceDataLine sourceDataLine;
    private final InputStream inputStream;
    private final int bufferSize;

    public SoundProducer(SourceDataLine sourceDataLine, InputStream inputStream, int bufferSize) {
        this.sourceDataLine = sourceDataLine;
        this.inputStream = inputStream;
        this.bufferSize = bufferSize;
    }

    @Override
    public void run() {
        try {
            sourceDataLine.start();
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int count = inputStream.read(buffer);
                sourceDataLine.write(buffer, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
}
