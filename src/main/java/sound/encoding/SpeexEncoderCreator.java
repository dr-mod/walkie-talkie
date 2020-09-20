package sound.encoding;

import org.xiph.speex.spi.SpeexEncoding;

import javax.sound.sampled.AudioFormat;

public class SpeexEncoderCreator extends EncoderCreator {

    private final SpeexEncoding speexEncoding;

    public SpeexEncoderCreator(SpeexEncoding speexEncoding) {
        this.speexEncoding = speexEncoding;
    }

    @Override
    public Encoder getEncoder(Quality quality) {
        int frameRate;
        switch (quality) {
            case LOW: frameRate = 8000; break;
            case MID: frameRate = 16000; break;
            case HIGH: frameRate = 32000; break;
            default: frameRate = 8000; break;
        }
        AudioFormat speexAudioFormat = new AudioFormat(speexEncoding, frameRate, -1, 1, -1, -1, false);
        AudioFormat pcmAudioFormat = new AudioFormat(frameRate, 16, 1, true, false);
        return new Encoder(pcmAudioFormat, speexAudioFormat);
    }
}
