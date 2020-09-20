package sound;

import javax.sound.sampled.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AudioHelper {

    private static Map<String, Mixer.Info> availableAudioInputs() {
        Map<String, Mixer.Info> results = new HashMap<>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getTargetLineInfo();
            if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
                results.put(info.getName(), info);
            }
        }
        return results;
    }

    private static Map<String, Mixer.Info> availableAudioOutputs() {
        Map<String, Mixer.Info> results = new HashMap<>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getSourceLineInfo();
            if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(SourceDataLine.class)) {
                results.put(info.getName(), info);
            }
        }
        return results;
    }

    public static Collection<String> audioInputs() {
        return availableAudioInputs().keySet();
    }

    public static Mixer.Info getAudioInput(String name) {
        return availableAudioInputs().get(name);
    }

    public static Mixer.Info getAudioOutput(String name) {
        return availableAudioOutputs().get(name);
    }

    public static Collection<String> audioOutputs() {
        return availableAudioOutputs().keySet();
    }

}
