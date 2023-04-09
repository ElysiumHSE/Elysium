package hse.elysium.firebaseInteractor;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class FirebaseService {

    enum UploadMode {
        AUDIO,
        COVER,
    }

    /**
     * Given a path to firebase access token and firebase project ID, performs authorization
     * for future uploads/downloads to firebase bucket.
     * @return Storage object which is used for uploads/downloads.
     */
    public Storage authorizeInFirebase(String pathToAccessToken, String projectId) throws IOException {
        FileInputStream serviceAccount = new FileInputStream(pathToAccessToken);

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId(projectId).build().getService();

        serviceAccount.close();

        return storage;
    }

    /**
     * Given a Storage object, name of firebase bucket, expected name of file in firebase, path to file on local machine
     * and UploadMode object, performs uploading of file to firebase bucket.
     * Expected name of file in firebase must be one of the following:
     * "audioFiles/sample.wav"
     * "coverFiles/sample.png".
     */
    public void uploadMedia(
            Storage storage, String bucketName, String objectName, String filePath, UploadMode mode) throws IOException {
        // Storage object.

        // The ID of your GCS bucket
        // String bucketName = "your-unique-bucket-name";

        // The ID of your GCS object
        // String objectName = "your-object-name";

        // The path to your file to upload
        // String filePath = "path/to/your/file"

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo;
        if (mode == UploadMode.AUDIO) {
            blobInfo = BlobInfo.newBuilder(blobId).setContentType("audio/wav").build();
        } else {
            blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();
        }

        // Optional: set a generation-match precondition to avoid potential race
        // conditions and data corruptions. The request returns a 412 error if the
        // preconditions are not met.
        Storage.BlobWriteOption precondition;
        if (storage.get(bucketName, objectName) == null) {
            // For a target object that does not yet exist, set the DoesNotExist precondition.
            // This will cause the request to fail if the object is created before the request runs.
            precondition = Storage.BlobWriteOption.doesNotExist();
        } else {
            // If the destination already exists in your bucket, instead set a generation-match
            // precondition. This will cause the request to fail if the existing object's generation
            // changes before the request runs.
            precondition =
                    Storage.BlobWriteOption.generationMatch(
                            storage.get(bucketName, objectName).getGeneration());
        }
        storage.createFrom(blobInfo, Paths.get(filePath), precondition);
    }

    /**
     * Given a Storage object, name of firebase bucket, expected name of file in firebase, path to file on local machine,
     * performs downloading of file from firebase bucket.
     * Path to file on local machine must include the filename, i.e. "path/to/file/sample.wav".
     */
    public void downloadMedia(Storage storage, String bucketName, String objectName, String destFilePath) {
        // Storage object.

        // The ID of your GCS bucket
        // String bucketName = "your-unique-bucket-name";

        // The ID of your GCS object
        // String objectName = "your-object-name";

        // The path to which the file should be downloaded
        // String destFilePath = "/local/path/to/file.txt";

        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        blob.downloadTo(Paths.get(destFilePath));
    }
}