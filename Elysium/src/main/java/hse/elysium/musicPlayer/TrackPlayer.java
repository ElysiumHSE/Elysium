package hse.elysium.musicPlayer;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class TrackPlayer {
    private Clip clip;

    private String status;

    private Long currentFrame = 0L;

    private boolean initialised = false;

    private boolean sourceChanged = false;

    private String filePath = null;

    public void stateHandler(int commandNumber) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        switch (commandNumber) {
            case 1:
                pause();
                break;
            case 2:
                resume();
                break;
            case 3:
                restart();
                break;
            case 4:
                if (clip == null) {
                    System.out.println("Audio file is not initialised, start playing something.");
                    break;
                }
                Scanner sc1 = new Scanner(System.in);
                System.out.println("Enter time in microseconds (" + 0 + ", " + clip.getMicrosecondLength() + "):");
                long timeCode = sc1.nextLong();
                jump(timeCode);
                break;
            case 5:
                Scanner sc2 = new Scanner(System.in);
                System.out.println("Enter path to a new audio file:");
                playNewTrack(sc2.next());
                break;
            case 6:
                stop();
                break;
        }
    }

    public void playNewTrack(String newFilePath) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        if (!initialised) {
            initialised = true;
            clip = AudioSystem.getClip();
        }

        if (filePath != null && !filePath.equals(newFilePath)) {
            sourceChanged = true;
        }

        filePath = newFilePath;
        reset();

        clip.start();
        status = "play";
    }

    public int pause() {
        if (!initialised) {
            System.out.println("Audio file is not initialised, start playing something.");
            return 1;
        }
        if (status.equals("paused")) {
            System.out.println("Already paused something, please ensure you've resumed.");
            return 1;
        }
        currentFrame = clip.getMicrosecondPosition();
        clip.stop();
        status = "paused";
        return 0;
    }

    public int resume() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (!initialised) {
            System.out.println("Audio file is not initialised, start playing something.");
            return 1;
        }
        if (status.equals("play")) {
            System.out.println("Already playing something, please ensure you've paused.");
            return 1;
        }
        clip.close();
        reset();
        clip.setMicrosecondPosition(currentFrame);
        playNewTrack(filePath);
        return 0;
    }

    public void restart() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (!initialised) {
            System.out.println("Audio file is not initialised, start playing something.");
            return;
        }
        clip.stop();
        reset();

        clip.setMicrosecondPosition(0);
        playNewTrack(filePath);
    }

    public void stop() {
        if (!initialised) {
            System.out.println("Audio file is not initialised, start playing something.");
            return;
        }
        currentFrame = 0L;
        clip.stop();
        clip.close();
    }

    public int jump(long c) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (!initialised) {
            System.out.println("Audio file is not initialised, start playing something.");
            return 1;
        }
        if (c > 0 && c < clip.getMicrosecondLength()) {
            stop();
            reset();

            currentFrame = c;
            clip.setMicrosecondPosition(c);
            playNewTrack(filePath);
            return 0;
        }
        System.out.println("Wrong timing entered, ensure input is correct.");
        return 1;
    }

    public void reset() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());

        if (!clip.isOpen()) {
            clip.open(audioInputStream);
        }
        if (sourceChanged) {
            stop();
            clip.open(audioInputStream);
            sourceChanged = false;
        }
    }
}
