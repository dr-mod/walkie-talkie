package sound.encoding;

import org.xiph.speex.spi.SpeexEncoding;

import java.util.Arrays;

public enum Codec {
    SPEEX_Q3("speex_q3", new SpeexEncoderCreator(SpeexEncoding.SPEEX_Q3)),
    SPEEX_Q5("speex_q5", new SpeexEncoderCreator(SpeexEncoding.SPEEX_Q5)),
    PCM("pcm", new PCMEncoderCreator());

    private String name;
    private EncoderCreator encoderCreator;

    Codec(String name, EncoderCreator formatCreator) {
        this.name = name;
        this.encoderCreator = formatCreator;
    }

    public EncoderCreator getEncoderCreator() {
        return encoderCreator;
    }
}
