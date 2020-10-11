package network;

import util.RingBuffer;

import java.io.IOException;
import java.io.InputStream;

public class DataReceiver extends Thread {

    private RingBuffer ringBuffer;
    private InputStream inputStream;

    public DataReceiver(RingBuffer ringBuffer, InputStream inputStream) {

        this.ringBuffer = ringBuffer;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[20];
            while (true) {
                int count = inputStream.read(buffer);
                ringBuffer.put(buffer, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
