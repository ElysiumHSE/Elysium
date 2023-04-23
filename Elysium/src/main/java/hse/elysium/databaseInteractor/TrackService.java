package hse.elysium.databaseInteractor;

import jakarta.persistence.*;

import hse.elysium.entities.Track;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrackService {
    private final EntityManagerFactory
            entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private final EntityManager entityManager = entityManagerFactory.createEntityManager();

    private final Query getMaxTrackId = entityManager.createNativeQuery
            ("SELECT MAX(track_id) from Track");

    @SuppressWarnings("unchecked")
    private final TypedQuery<Track> getTracksWithTrackIdsQuery =
            (TypedQuery<Track>)entityManager.createNativeQuery
                    ("SELECT * FROM Track where track_id IN :track_id_array", Track.class);

    @SuppressWarnings("unchecked")
    private final TypedQuery<Track> getAllTracksQuery =
            (TypedQuery<Track>)entityManager.createNativeQuery
                    ("SELECT * FROM Track", Track.class);

    /**
     * Given Set of track_id's, finds matching records with corresponding track_id's
     * in Track database table.
     * @return List of Track objects, if at least one track_id from given Set was matched successfully,
     * and null, if no matches were found.
     */
    public List<Track> getTracksWithTrackIds(List<Integer> arrayOfTrackIds) {
        EntityTransaction transaction = entityManager.getTransaction();

        ArrayList<Track> array;

        try {
            transaction.begin();

            getTracksWithTrackIdsQuery.setParameter("track_id_array", arrayOfTrackIds);
            array = new ArrayList<>(getTracksWithTrackIdsQuery.getResultList());

            transaction.commit();

        } catch (jakarta.persistence.NoResultException e) {
            return null;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return array;
    }

    /**
     * Finds tracks in Track database table.
     * @return List of Track objects, if at least one track is present,
     * and null, if no matches were found.
     */
    public List<Track> getAllTracks() {
        EntityTransaction transaction = entityManager.getTransaction();

        ArrayList<Track> array;

        try {
            transaction.begin();

            array = new ArrayList<>(getAllTracksQuery.getResultList());

            transaction.commit();

        } catch (jakarta.persistence.NoResultException e) {
            return null;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return array;
    }

    /**
     * Given a track_id, finds matching record in Track database table.
     * @return Object of class Track, if matching record was found, and null otherwise.
     */
    public Track getTrackWithTrackId(int track_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        Track track;

        try {
            transaction.begin();

            track = entityManager.find(Track.class, track_id);

            transaction.commit();

        } catch (NoResultException | IllegalArgumentException e) {
            return null;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return track;
    }

    /**
     * Given name, author, genre, mood, music_url and cover_url, adds a new record with given parameters
     * to Track database table. Value of streams of a new track record is set 0.
     * @return track_id of new record
     */
    public int addNewTrackWithAllParams(String name, String author, String genre, String mood,
                                        String music_url, String cover_url) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Track track = new Track();
            track.setName(name);
            track.setAuthor(author);
            track.setGenre(genre);
            track.setMood(mood);
            track.setMusicUrl(music_url);
            track.setCoverUrl(cover_url);
            track.setStreams(0);

            entityManager.merge(track);

            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return (int)getMaxTrackId.getSingleResult();
    }

    /**
     * Given name, author, genre and mood, adds a new record with given parameters
     * to Track database table. Values of music_url and cover_url are set null.
     * Value of streams of a new track record is set 0.
     * @return track_id of new record
     */
    public int addNewTrackWithNameAuthorGenreMood(String name, String author, String genre, String mood) {
        return addNewTrackWithAllParams(name, author, genre, mood, null, null);
    }

    /**
     * Given Track object, updates parameters in matching record of Track database table.
     * @return true, if parameters were updated successfully, and false,
     * if track_id of given Track object did not match any of Track database table records or
     * given Track object is null.
     */
    public boolean updateTrackAllParamsWithUpdatedTrack(Track updated_track) {
        if (updated_track == null) {
            return false;
        }

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Track track = entityManager.getReference(Track.class, updated_track.getTrackId());
        if (track == null) {
            return false;
        }

        track.setName(updated_track.getName());
        track.setAuthor(updated_track.getAuthor());
        track.setGenre(updated_track.getGenre());
        track.setMood(updated_track.getMood());
        track.setMusicUrl(updated_track.getMusicUrl());
        track.setCoverUrl(updated_track.getCoverUrl());
        track.setStreams(updated_track.getStreams());

        transaction.commit();

        return true;
    }

    /**
     * Given track_id, deletes matching record of Track database table.
     * @return Object of class Track representing the deleted record, if matching record was found, and null otherwise.
     */
    public Track deleteTrackWithTrackId(int track_id) {
        Track track = getTrackWithTrackId(track_id);

        if (track == null) {
            return null;
        }

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        entityManager.remove(track);

        transaction.commit();

        return track;
    }

    /**
     * Given track_id, increments number of streams in matching record of Track database table.
     * @return true, if streams of matching record was incremented successfully,
     * and false, if matching record was not found.
     */
    public boolean incrementStreamsWithTrackId(int track_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Track track = entityManager.getReference(Track.class, track_id);

            track.setStreams(track.getStreams() + 1);

            transaction.commit();

        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return false;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return true;
    }

    /**
     * Close entity manager and entity manager factory when finished working with class.
     */
    public void closeHandler() {
        entityManager.close();
        entityManagerFactory.close();
    }
}