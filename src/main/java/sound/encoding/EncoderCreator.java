package sound.encoding;

public abstract class EncoderCreator {
    public abstract Encoder getEncoder(Quality quality);
}
