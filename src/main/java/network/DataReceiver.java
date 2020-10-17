package network;

import config.Configuration;
import util.RingBuffer;

import java.io.IOException;
import java.io.InputStream;

public class DataReceiver extends Thread {

    private RingBuffer ringBuffer;
    private InputStream inputStream;
    private Configuration configuration;

    public DataReceiver(RingBuffer ringBuffer, InputStream inputStream, Configuration configuration) {
        this.ringBuffer = ringBuffer;
        this.inputStream = inputStream;
        this.configuration = configuration;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[configuration.getBufferSize()];
            while (true) {
                int count = inputStream.read(buffer);
                ringBuffer.put(buffer, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
