package sound.encoding;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import java.io.InputStream;

public class Encoder {
    private AudioFormat original;
    private AudioFormat converted;

    public Encoder(AudioFormat original) {
        this(original, null);
    }

    public Encoder(AudioFormat original, AudioFormat converted) {
        this.original = original;
        this.converted = converted;
    }

    public AudioInputStream transmitting(TargetDataLine microphone) {
        if (original != null && converted == null) {
            return new AudioInputStream(microphone);
        } else if (original != null && converted != null) {
            AudioInputStream audioInputStream = new AudioInputStream(microphone);
            return AudioSystem.getAudioInputStream(converted, audioInputStream);
        }
        throw new IllegalArgumentException("Not enough audio formats");
    }


    public AudioInputStream receiving(InputStream inputStream) {
        if (original != null && converted == null) {
            return new AudioInputStream(inputStream, original, -1);
        } else if (original != null && converted != null) {
            AudioInputStream audioInputStream = new AudioInputStream(inputStream, converted, -1);
            return AudioSystem.getAudioInputStream(original, audioInputStream);
        }
        throw new IllegalArgumentException("Not enough audio formats");
    }

    public AudioFormat getOriginalAudioFormat() {
        return original;
    }
}