package sound;

import config.Configuration;

import javax.sound.sampled.*;

public class AudioIOAcquirer {

    private AudioFormat format;
    private Configuration configuration;

    public AudioIOAcquirer(AudioFormat format, Configuration configuration) {
        this.format = format;
        this.configuration = configuration;
    }

    public TargetDataLine getMicrophone() throws LineUnavailableException {
        DataLine.Info microphoneInfo = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(microphoneInfo);
        microphone.open(format, configuration.getMicrophoneBufferSize());
        return microphone;
    }

    public TargetDataLine getMicrophone(String name) throws LineUnavailableException {
        TargetDataLine microphone = AudioSystem.getTargetDataLine(format, AudioHelper.getAudioInput(name));
        microphone.open(format, configuration.getMicrophoneBufferSize());
        return microphone;
    }

    public SourceDataLine getSpeakers() throws LineUnavailableException {
        DataLine.Info speakersInfo = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(speakersInfo);
        speakers.open(format, configuration.getSpeakersBufferSize());
        return speakers;
    }

    public SourceDataLine getSpeakers(String name) throws LineUnavailableException {
        SourceDataLine speakers = AudioSystem.getSourceDataLine(format, AudioHelper.getAudioOutput(name));
        speakers.open(format, configuration.getSpeakersBufferSize());
        return speakers;
    }

}
