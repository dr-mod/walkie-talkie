import config.Configuration;
import network.DataReceiver;
import org.apache.commons.cli.*;
import security.EncryptionLayer;
import sound.AudioHelper;
import sound.AudioIOAcquirer;
import sound.SoundAcquirer;
import sound.SoundConsumer;
import sound.encoding.Codec;
import sound.encoding.Encoder;
import sound.encoding.EncoderCreator;
import util.RingBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Application {

    public static void main(String[] args) throws Exception {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        if (line.hasOption("help") || line.getOptions().length == 0) {
            showHelp(options);
            System.exit(0);
        }

        if (line.hasOption("devices")) {
            printAvailableDevices();
            System.exit(0);
        }

        String config = null;
        if (line.hasOption("config")) {
            config = line.getOptionValue("config");
        } else {
            System.out.println("error: no config file provided");
            System.exit(-1);
        }

        int port = 9046;
        if (line.hasOption("port")) {
            port = Integer.parseInt(line.getOptionValue("port"));
        }

        boolean server = line.hasOption("server");
        String link = null;
        if (!server) {
            if (line.hasOption("link")) {
                link = line.getOptionValue("link");
            } else {
                System.out.println("error: link has to be provided in client mode");
                System.exit(-1);
            }
        }

        Configuration configuration = new Configuration(new FileInputStream(config));

        Codec codec = configuration.getCodec();
        EncoderCreator encoderCreator = codec.getEncoderCreator();
        Encoder encoder = encoderCreator.getEncoder(configuration.getQuality());

        AudioFormat originalAudioFormat = encoder.getOriginalAudioFormat();
        AudioIOAcquirer audioIOAcquirer = new AudioIOAcquirer(originalAudioFormat, configuration);

        String microphoneName = configuration.getMicrophoneName();
        TargetDataLine microphone = microphoneName == null ? audioIOAcquirer.getMicrophone() : audioIOAcquirer.getMicrophone(microphoneName);
        String speakersName = configuration.getSpeakersName();
        SourceDataLine speakers = speakersName == null ? audioIOAcquirer.getSpeakers() : audioIOAcquirer.getSpeakers(speakersName);

        EncryptionLayer encryptionLayer = new EncryptionLayer(configuration.getPassword());

        Socket socket;
        if (server) {
            System.out.println("Starting in server mode..");
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Waiting for connection..");
            socket = serverSocket.accept();

        } else {
            System.out.println("Starting in client mode..");
            System.out.println("Establishing connection..");
            socket = new Socket(link, port);
        }
        System.out.println("Connection established: " + socket.getRemoteSocketAddress());
        run(configuration, encoder, microphone, speakers, encryptionLayer, socket);
    }

    private static void run(Configuration configuration, Encoder encoder, TargetDataLine microphone,
                            SourceDataLine speakers, EncryptionLayer encryptionLayer, Socket socket) throws Exception {
        AudioInputStream microphoneInputStream = encoder.transmitting(microphone);
        OutputStream outputStream = encryptionLayer.secureOutputStream(socket.getOutputStream());

        InputStream inputStream = encryptionLayer.secureInputStream(socket.getInputStream());
        AudioInputStream receivingAudio = encoder.receiving(inputStream);

        RingBuffer ringBuffer = new RingBuffer(configuration.getRingBufferSize());
        new DataReceiver(ringBuffer, receivingAudio).start();

        new SoundAcquirer(microphone, microphoneInputStream, outputStream, configuration).start();
        new SoundConsumer(speakers, ringBuffer, configuration).start();
    }

    private static Options getOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "show help");
        options.addOption("s", "server", false, "set mode of operation to server");
        options.addOption("p", "port", true, "port used by application");
        options.addOption("l", "link", true, "ip or host of server");
        options.addOption("d", "devices", false, "show list of available devices");
        options.addOption("c", "config", true, "path to configuration file");
        return options;
    }

    private static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("walkie-talkie", options);
    }

    private static void printAvailableDevices() {
        System.out.println("Inputs:");
        AudioHelper.audioInputs().forEach(System.out::println);
        System.out.println();
        System.out.println("Outputs:");
        AudioHelper.audioOutputs().forEach(System.out::println);
    }
}
