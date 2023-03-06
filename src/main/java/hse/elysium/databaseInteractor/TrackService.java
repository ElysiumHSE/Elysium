package hse.elysium.databaseInteractor;

import jakarta.persistence.*;

import hse.elysium.entities.Track;

import java.util.HashSet;
import java.util.Set;

public class TrackService {
    private static final EntityManagerFactory
        entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private static final EntityManager entityManager = entityManagerFactory.createEntityManager();

    private static final Query getMaxTrackId = entityManager.createNativeQuery
        ("SELECT MAX(track_id) from Track");

    @SuppressWarnings("unchecked")
    private static final TypedQuery<Track> getTracksWithTrackIdsQuery =
        (TypedQuery<Track>)entityManager.createNativeQuery
        ("SELECT * FROM Track where track_id IN :track_id_set", Track.class);

    /**
     * Given Set of track_id's, finds matching records with corresponding track_id's
     * in Track database table.
     * @return Set of Track objects, if at least one track_id from given Set was matched successfully,
     * and null, if no matches were found.
     */
    public static Set<Track> getTracksWithTrackIds(Set<Integer> setOfTrackIds) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            getTracksWithTrackIdsQuery.setParameter("track_id_set", setOfTrackIds);
            Set<Track> set = new HashSet<>(getTracksWithTrackIdsQuery.getResultList());

            transaction.commit();

            return set;

        } catch (jakarta.persistence.NoResultException e) {
            return null;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    /**
     * Given a track_id, finds matching record in Track database table.
     * @return Object of class Track, if matching record was found, and null otherwise.
     */
    public static Track getTrackWithTrackId(int track_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Track track = entityManager.find(Track.class, track_id);

            transaction.commit();

            return track;

        } catch (jakarta.persistence.NoResultException e) {
            return null;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    /**
     * Given name, author, genre, mood, music_url and cover_url, adds a new record with given parameters
     * to Track database table. Value of streams of a new track record is set 0.
     * @return track_id of new record, if new record table was added to Track database successfully, and 0 otherwise.
     * May throw hibernate exceptions (FIXME).
     */
    public static int addNewTrackWithAllParams(String name, String author, String genre, String mood,
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
        } catch (Throwable e) {
            return 0;

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
     * @return track_id of new record, if new record table was added to Track database successfully, and 0 otherwise.
     * May throw hibernate exceptions (FIXME).
     */
    public static int addNewTrackWithNameAuthorGenreMood(String name, String author, String genre, String mood) {
        return addNewTrackWithAllParams(name, author, genre, mood, null, null);
    }

    /**
     * Given Track object, updates parameters in matching record of Track database table.
     * @return 1, if parameters were updated successfully, and 0,
     * if track_id of given Track object did not match any of Track database table records.
     */
    public static int updateTrackAllParamsWithUpdatedTrack(Track updated_track) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Track track = entityManager.getReference(Track.class, updated_track.getTrackId());
            if (track == null) {
                return 0;
            }

            track.setName(updated_track.getName());
            track.setAuthor(updated_track.getAuthor());
            track.setGenre(updated_track.getGenre());
            track.setMood(updated_track.getMood());
            track.setMusicUrl(updated_track.getMusicUrl());
            track.setCoverUrl(updated_track.getCoverUrl());
            track.setStreams(updated_track.getStreams());

            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return 1;
    }

    /**
     * Given track_id, deletes matching record of Track database table.
     * @return Object of class Track representing the deleted record, if matching record was found, and null otherwise.
     */
    public static Track deleteTrackWithTrackId(int track_id) {
        Track track = getTrackWithTrackId(track_id);

        if (track == null) {
            return null;
        }

        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.remove(track);

            transaction.commit();

            return track;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    /**
     * Given track_id, increments number of streams in matching record of Track database table.
     * @return 1, if streams of matching record was incremented successfully, and 0, if matching record was not found.
     */
    public static int incrementStreamsWithTrackId(int track_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Track track = entityManager.getReference(Track.class, track_id);

            track.setStreams(track.getStreams() + 1);

            transaction.commit();

            return 1;

        } catch (jakarta.persistence.NoResultException e) {
            return 0;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    /**
     * Close entity manager and entity manager factory when finished working with class.
     */
    public static void closeHandler() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
