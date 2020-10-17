package sound;

import config.Configuration;
import util.RingBuffer;

import javax.sound.sampled.SourceDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

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
        ArrayList<Integer> integers = new ArrayList<>();
        try {
            sourceDataLine.start();
            byte[] buffer = new byte[configuration.getBufferSize()];
            ByteBuffer bb = ByteBuffer.allocate(buffer.length).order(ByteOrder.LITTLE_ENDIAN);


            int offset = 0;
            while (true) {
                int count = ringBuffer.get(buffer, offset, buffer.length - offset);
                int bestPlaceToCut = findBestPlaceToCut(buffer, count + offset, bb);

                sourceDataLine.write(buffer, 0, bestPlaceToCut);

                offset = count + offset - bestPlaceToCut;
                if (offset > 0) {
                    System.arraycopy(buffer, bestPlaceToCut, buffer, 0, offset);
                }


                integers.add(bestPlaceToCut);
                if (integers.size() > 1000) {
                    integers.forEach(System.out::println);
                    integers.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    private int findBestPlaceToCut(byte[] buffer, int count, ByteBuffer bb) {
        short[] shorts = new short[buffer.length / 2];
        bb.clear();
        bb.put(buffer, 0, count);
        bb.rewind();

        bb.asShortBuffer().get(shorts);

        int cutPosition = count / 2;
        for (int i = count / 2 - 1 - 1; i >= 0; i--) {
            if (!sameSign(shorts[i + 1], shorts[i])) {
                cutPosition = i;
                break;
            }
        }

        int byteResult = cutPosition * 2;
        return byteResult > 0 ? byteResult : count;
    }

    boolean sameSign(int x, int y) {
        return (x >= 0) ^ (y < 0);
    }

}
