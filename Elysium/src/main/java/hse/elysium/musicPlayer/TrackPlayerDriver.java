package hse.elysium.musicPlayer;

import java.util.Scanner;

public class TrackPlayerDriver {

    public static void main(String[] args) {
        try {
            TrackPlayer audioPlayer = new TrackPlayer();

            // filePath = "./src/main/java/hse/elysium/musicPlayer/audioFiles/sampleAudio.wav";
            // filePath2 = "./src/main/java/hse/elysium/musicPlayer/audioFiles/sampleAudio2.wav";

            // Still not working with links
            // FIXME filePath3 = "https://drive.google.com/file/d/10S3LL6r8-ZPXSaM4WUnZAe7Se4Z1o56r/view?usp=share_link";

            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("1. Pause");
                System.out.println("2. Resume");
                System.out.println("3. Restart");
                System.out.println("4. Jump to specific time");
                System.out.println("5. Play new audio file");
                System.out.println("6. Stop everything");
                int command = sc.nextInt();
                audioPlayer.stateHandler(command);
                if (command == 6) {
                    break;
                }
            }
            sc.close();

        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
}
