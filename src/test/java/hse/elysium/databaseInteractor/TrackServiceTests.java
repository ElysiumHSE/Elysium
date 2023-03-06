package hse.elysium.databaseInteractor;

import hse.elysium.entities.Track;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class TrackServiceTests {
    @Test
    public void addGetDeleteTest() {
        int track_id = TrackService.addNewTrackWithNameAuthorGenreMood
                       ("Рассвет", "Джизус", "Rock", "Sad");
        Assertions.assertNotEquals(0, track_id);

        Track track = TrackService.getTrackWithTrackId(track_id);
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
        Assertions.assertEquals(1, TrackService.updateTrackAllParamsWithUpdatedTrack(track));

        track = TrackService.getTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);

        Assertions.assertEquals("Рассвет", track.getName());
        Assertions.assertEquals("Джизус", track.getAuthor());
        Assertions.assertEquals("Rock", track.getGenre());
        Assertions.assertEquals("Sad", track.getMood());
        Assertions.assertEquals("sample/music/url", track.getMusicUrl());
        Assertions.assertEquals("sample/cover/url", track.getCoverUrl());
        Assertions.assertEquals(0, track.getStreams());

        track = TrackService.deleteTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);

        Assertions.assertEquals("Рассвет", track.getName());
        Assertions.assertEquals("Джизус", track.getAuthor());
        Assertions.assertEquals("Rock", track.getGenre());
        Assertions.assertEquals("Sad", track.getMood());
        Assertions.assertEquals("sample/music/url", track.getMusicUrl());
        Assertions.assertEquals("sample/cover/url", track.getCoverUrl());
        Assertions.assertEquals(0, track.getStreams());

        track = TrackService.deleteTrackWithTrackId(track_id);
        Assertions.assertNull(track);
    }

    @Test
    public void incrementTest() {
        int track_id = TrackService.addNewTrackWithNameAuthorGenreMood
                       ("Рассвет", "Джизус", "Rock", "Sad");
        Assertions.assertNotEquals(0, track_id);

        Assertions.assertEquals(1, TrackService.incrementStreamsWithTrackId(track_id));

        Track track = TrackService.getTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);

        Assertions.assertEquals("Рассвет", track.getName());
        Assertions.assertEquals("Джизус", track.getAuthor());
        Assertions.assertEquals("Rock", track.getGenre());
        Assertions.assertEquals("Sad", track.getMood());
        Assertions.assertNull(track.getMusicUrl());
        Assertions.assertNull(track.getCoverUrl());
        Assertions.assertEquals(1, track.getStreams());

        track = TrackService.deleteTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);

        Assertions.assertEquals("Рассвет", track.getName());
        Assertions.assertEquals("Джизус", track.getAuthor());
        Assertions.assertEquals("Rock", track.getGenre());
        Assertions.assertEquals("Sad", track.getMood());
        Assertions.assertNull(track.getMusicUrl());
        Assertions.assertNull(track.getCoverUrl());
        Assertions.assertEquals(1, track.getStreams());
    }

    @Test
    public void getTracksWithTrackIdsTest() {
        int track_id = TrackService.addNewTrackWithNameAuthorGenreMood
                        ("Рассвет", "Джизус", "Rock", "Sad");
        Assertions.assertNotEquals(0, track_id);

        Set<Integer> set = new HashSet<>();
        set.add(track_id);
        Set<Track> tracks = TrackService.getTracksWithTrackIds(set);
        for (Track track : tracks) {
            Assertions.assertEquals("Рассвет", track.getName());
            Assertions.assertEquals("Джизус", track.getAuthor());
            Assertions.assertEquals("Rock", track.getGenre());
            Assertions.assertEquals("Sad", track.getMood());
            Assertions.assertNull(track.getMusicUrl());
            Assertions.assertNull(track.getCoverUrl());
            Assertions.assertEquals(0, track.getStreams());
        }

        Track track = TrackService.deleteTrackWithTrackId(track_id);
        Assertions.assertNotEquals(null, track);
    }
}

