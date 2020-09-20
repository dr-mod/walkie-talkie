package sound.encoding;

import javax.sound.sampled.AudioFormat;

public class PCMEncoderCreator extends EncoderCreator {

    @Override
    public Encoder getEncoder(Quality quality) {
        int frameRate;
        switch (quality) {
            case LOW: frameRate = 8000; break;
            case MID: frameRate = 16000; break;
            case HIGH: frameRate = 32000; break;
            default: frameRate = 8000; break;
        }
        AudioFormat audioFormat = new AudioFormat(frameRate, 16, 1, true, false);
        return new Encoder(audioFormat);
    }
}
