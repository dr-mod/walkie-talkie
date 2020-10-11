package sound.encoding;

import javax.sound.sampled.AudioFormat;

public class PCMEncoderCreator extends EncoderCreator {

    @Override
    public Encoder getEncoder(Quality quality) {
        int sampleRate;
        switch (quality) {
            case LOW: sampleRate = 8000; break;
            case MID: sampleRate = 16000; break;
            case HIGH: sampleRate = 32000; break;
            default: sampleRate = 8000; break;
        }
        AudioFormat audioFormat = new AudioFormat(sampleRate, 16, 1, true, false);
        return new Encoder(audioFormat);
    }
}
