package hse.elysium.firebaseInteractor;

import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FirebaseServiceTests {
    @Test
    public void allFunctionsTest() throws IOException {
        FirebaseService firebaseService = new FirebaseService();

        Storage storage = firebaseService.authorizeInFirebase(
                "./src/main/java/hse/elysium/firebaseToken/sublime-state-277509-firebase-adminsdk-sc29o-6740f9a7ef.json",
                "sublime-state-277509");

        firebaseService.uploadMedia(storage, "sublime-state-277509.appspot.com",
                "audioFiles/sampleAudio.wav",
                "./src/main/java/hse/elysium/mediaSource/audioFiles/sampleAudio.wav", FirebaseService.UploadMode.AUDIO);

        firebaseService.downloadMedia(storage, "sublime-state-277509.appspot.com",
                "audioFiles/sampleAudio.wav",
                "./src/main/java/hse/elysium/mediaSource/audioFiles/sampleAudio.wav");

        firebaseService.uploadMedia(storage, "sublime-state-277509.appspot.com",
                "coverFiles/default_cover.png",
                "./src/main/java/hse/elysium/mediaSource/coverFiles/default_cover.png", FirebaseService.UploadMode.COVER);

        firebaseService.downloadMedia(storage, "sublime-state-277509.appspot.com",
                "coverFiles/default_cover.png",
                "./src/main/java/hse/elysium/mediaSource/coverFiles/default_cover.png");
    }
}
