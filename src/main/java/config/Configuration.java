package config;

import sound.encoding.Codec;
import sound.encoding.Quality;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

public class Configuration {

    private String microphoneName;
    private String speakersName;
    private Codec codecName;
    private Quality quality;
    private char[] password;
    private int bufferSize;
    private int microphoneBufferSize;
    private int speakersBufferSize;
    private int ringBufferSize;
    private int soundThreshold;
    private boolean emptyFrame;

    public Configuration(InputStream inputStream) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("Couldn't load properties, defaults will be used.");
        }
        parseProperties(properties);
    }

    private void parseProperties(Properties properties) {
        this.microphoneName = Optional.ofNullable(properties.getProperty("audio.input"))
                .filter(it -> !it.isBlank())
                .orElse(null);
        this.speakersName = Optional.ofNullable(properties.getProperty("audio.output"))
                .filter(it -> !it.isBlank())
                .orElse(null);
        this.codecName = Optional.ofNullable(properties.getProperty("audio.codec"))
                .flatMap(it -> Arrays.stream(Codec.values())
                        .filter(codec -> codec.name().equalsIgnoreCase(it))
                        .findFirst())
                .orElse(Codec.PCM);
        this.quality = Optional.ofNullable(properties.getProperty("audio.quality"))
                .flatMap(it -> Arrays.stream(Quality.values())
                        .filter(codec -> codec.name().equalsIgnoreCase(it))
                        .findFirst())
                .orElse(Quality.MID);
        this.soundThreshold = Optional.ofNullable(properties.getProperty("audio.threshold"))
                .map(Integer::parseInt)
                .orElse(500);
        this.password = Optional.ofNullable(properties.getProperty("security.password"))
                .map(String::toCharArray)
                .orElseThrow(() -> new RuntimeException("Password is not provided"));
        this.bufferSize = Optional.ofNullable(properties.getProperty("io.buffer.size"))
                .map(Integer::parseInt)
                .orElse(50);
        this.microphoneBufferSize = Optional.ofNullable(properties.getProperty("io.buffer.microphone"))
                .map(Integer::parseInt)
                .orElse(512);
        this.speakersBufferSize = Optional.ofNullable(properties.getProperty("io.buffer.speakers"))
                .map(Integer::parseInt)
                .orElse(512);
        this.ringBufferSize = Optional.ofNullable(properties.getProperty("io.buffer.ring"))
                .map(Integer::parseInt)
                .orElse(512);
        this.emptyFrame = Optional.ofNullable(properties.getProperty("audio.empty-frame"))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public String getMicrophoneName() {
        return microphoneName;
    }

    public String getSpeakersName() {
        return speakersName;
    }

    public Codec getCodec() {
        return codecName;
    }

    public Quality getQuality() {
        return quality;
    }

    public char[] getPassword() {
        return password;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public int getMicrophoneBufferSize() {
        return microphoneBufferSize;
    }

    public int getSpeakersBufferSize() {
        return speakersBufferSize;
    }

    public int getRingBufferSize() {
        return ringBufferSize;
    }

    public int getSoundThreshold() {
        return soundThreshold;
    }

    public boolean isEmptyFrame() {
        return emptyFrame;
    }
}
