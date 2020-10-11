package sound;

import config.Configuration;

import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class SoundAcquirer extends Thread {
    private final TargetDataLine microphoneInput;
    private final InputStream inputStream;
    private final OutputStream networkOutputStream;
    private final Configuration configuration;

    public SoundAcquirer(TargetDataLine microphoneInput, InputStream inputStream, OutputStream networkOutputStream, Configuration configuration) {
        this.microphoneInput = microphoneInput;
        this.inputStream = inputStream;
        this.networkOutputStream = networkOutputStream;
        this.configuration = configuration;
    }

    @Override
    public void run() {
        try {
            microphoneInput.start();
            byte[] buffer = new byte[configuration.getBufferSize()];
            int audioThreshHold = configuration.getSoundThreshold();

            ByteBuffer bb = ByteBuffer.allocate(buffer.length);
            while (true) {
                int count = inputStream.read(buffer);


                bb.clear();
                bb.put(buffer, 0, count);
                bb.rewind();
                boolean audible = false;
                while (bb.hasRemaining()) {
                    if (Math.abs(bb.getShort()) > audioThreshHold) {
                        audible = true;
                        break;
                    }
                }

                if (audible) {
                    networkOutputStream.write(buffer, 0, count);
                }
//                else {
//                    System.out.println("nope");
//                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
