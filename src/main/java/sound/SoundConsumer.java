package sound;

import config.Configuration;
import util.RingBuffer;

import javax.sound.sampled.SourceDataLine;

public class SoundConsumer extends Thread {
    private final SourceDataLine sourceDataLine;
    private final RingBuffer ringBuffer;
    private final Configuration configuration;

    public SoundConsumer(SourceDataLine sourceDataLine, RingBuffer ringBuffer, Configuration configuration) {
        this.sourceDataLine = sourceDataLine;
        this.ringBuffer = ringBuffer;
        this.configuration = configuration;
    }

    @Override
    public void run() {
        byte[] empty = {1, 0, 0, 0, 0, 0};
        boolean emptyFrame = configuration.isEmptyFrame();
        try {
            sourceDataLine.start();
            byte[] buffer = new byte[configuration.getBufferSize()];

            while (true) {
                int count = ringBuffer.get(buffer);
                if (count == 0) {
                    if (emptyFrame) {
                        sourceDataLine.write(empty, 0, 6);
                    } else {
                        Thread.sleep(6);
                    }
                } else {
                    sourceDataLine.write(buffer, 0, count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
}
