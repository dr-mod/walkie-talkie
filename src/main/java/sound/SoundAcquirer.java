package sound;

import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SoundAcquirer extends Thread {
    private final TargetDataLine microphoneInput;
    private final InputStream inputStream;
    private final OutputStream networkOutputStream;
    private final int bufferSize;

    public SoundAcquirer(TargetDataLine microphoneInput, InputStream inputStream, OutputStream networkOutputStream, int bufferSize) {
        this.microphoneInput = microphoneInput;
        this.inputStream = inputStream;
        this.networkOutputStream = networkOutputStream;
        this.bufferSize = bufferSize;
    }

    @Override
    public void run() {
        try {
            microphoneInput.start();
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int count = inputStream.read(buffer);
                networkOutputStream.write(buffer, 0, count);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
