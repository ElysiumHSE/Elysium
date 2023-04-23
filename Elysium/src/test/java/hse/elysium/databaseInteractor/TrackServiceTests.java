package hse.elysium.databaseInteractor;

import hse.elysium.entities.Track;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TrackServiceTests {
    @Test
    public void addGetDeleteTest() {
        TrackService ts = new TrackService();
        int track_id = ts.addNewTrackWithNameAuthorGenreMood
                ("Рассвет", "Джизус", "Rock", "Sad");
        Assertions.assertNotEquals(0, track_id);

        Track track = ts.getTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);

        Assertions.assertEquals("Рассвет", track.getName());
        Assertions.assertEquals("Джизус", track.getAuthor());
        Assertions.assertEquals("Rock", track.getGenre());
        Assertions.assertEquals("Sad", track.getMood());
        Assertions.assertNull(track.getMusicUrl());
        Assertions.assertNull(track.getCoverUrl());
        Assertions.assertEquals(0, track.getStreams());

        track.setMusicUrl("sample/music/url");
        track.setCoverUrl("sample/cover/url");
        Assertions.assertTrue(ts.updateTrackAllParamsWithUpdatedTrack(track));

        track = ts.getTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);

        Assertions.assertEquals("Рассвет", track.getName());
        Assertions.assertEquals("Джизус", track.getAuthor());
        Assertions.assertEquals("Rock", track.getGenre());
        Assertions.assertEquals("Sad", track.getMood());
        Assertions.assertEquals("sample/music/url", track.getMusicUrl());
        Assertions.assertEquals("sample/cover/url", track.getCoverUrl());
        Assertions.assertEquals(0, track.getStreams());

        track = ts.deleteTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);

        Assertions.assertEquals("Рассвет", track.getName());
        Assertions.assertEquals("Джизус", track.getAuthor());
        Assertions.assertEquals("Rock", track.getGenre());
        Assertions.assertEquals("Sad", track.getMood());
        Assertions.assertEquals("sample/music/url", track.getMusicUrl());
        Assertions.assertEquals("sample/cover/url", track.getCoverUrl());
        Assertions.assertEquals(0, track.getStreams());

        track = ts.deleteTrackWithTrackId(track_id);
        Assertions.assertNull(track);

        ts.closeHandler();
    }

    @Test
    public void incrementTest() {
        TrackService ts = new TrackService();
        int track_id = ts.addNewTrackWithNameAuthorGenreMood
                ("Рассвет", "Джизус", "Rock", "Sad");
        Assertions.assertNotEquals(0, track_id);

        Assertions.assertTrue(ts.incrementStreamsWithTrackId(track_id));

        Track track = ts.getTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);

        Assertions.assertEquals("Рассвет", track.getName());
        Assertions.assertEquals("Джизус", track.getAuthor());
        Assertions.assertEquals("Rock", track.getGenre());
        Assertions.assertEquals("Sad", track.getMood());
        Assertions.assertNull(track.getMusicUrl());
        Assertions.assertNull(track.getCoverUrl());
        Assertions.assertEquals(1, track.getStreams());

        track = ts.deleteTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);

        Assertions.assertEquals("Рассвет", track.getName());
        Assertions.assertEquals("Джизус", track.getAuthor());
        Assertions.assertEquals("Rock", track.getGenre());
        Assertions.assertEquals("Sad", track.getMood());
        Assertions.assertNull(track.getMusicUrl());
        Assertions.assertNull(track.getCoverUrl());
        Assertions.assertEquals(1, track.getStreams());

        ts.closeHandler();
    }

    @Test
    public void getTracksWithTrackIdsTest() {
        TrackService ts = new TrackService();
        int track_id = ts.addNewTrackWithNameAuthorGenreMood
                ("Рассвет", "Джизус", "Rock", "Sad");
        Assertions.assertNotEquals(0, track_id);

        ArrayList<Integer> array = new ArrayList<>();
        array.add(track_id);
        List<Track> tracks = ts.getTracksWithTrackIds(array);
        Assertions.assertEquals(1, tracks.size());

        Track track = tracks.get(0);

        Assertions.assertEquals("Рассвет", track.getName());
        Assertions.assertEquals("Джизус", track.getAuthor());
        Assertions.assertEquals("Rock", track.getGenre());
        Assertions.assertEquals("Sad", track.getMood());
        Assertions.assertNull(track.getMusicUrl());
        Assertions.assertNull(track.getCoverUrl());
        Assertions.assertEquals(0, track.getStreams());

        track = ts.deleteTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);

        ts.closeHandler();
    }

    @Test
    public void getAllTracksDummyTest() {
        TrackService ts = new TrackService();
        List<Track> result = ts.getAllTracks();
        Assertions.assertNotNull(result);
        // Assertions.assertEquals(4, result.size());
    }
}
