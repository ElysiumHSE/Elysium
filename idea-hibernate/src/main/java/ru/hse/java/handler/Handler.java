package ru.hse.java.handler;

import jakarta.persistence.*;

import ru.hse.java.entities.Track;
import ru.hse.java.entities.User;

import java.util.HashSet;
import java.util.Set;

public class Handler {
    private static final EntityManagerFactory
        entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private static final EntityManager entityManager = entityManagerFactory.createEntityManager();

    private static final Query getUserIdWithLogin = entityManager.createNativeQuery
        ("SELECT user_id from User where User.login=:login");

    @SuppressWarnings("unchecked")
    private static final TypedQuery<Track> getTracksWithTrackIds = (TypedQuery<Track>) entityManager.createNativeQuery
        ("SELECT * FROM Track where track_id IN :track_id_set", Track.class);

    public static User getUserWithUserId(int user_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            User user = entityManager.find(User.class, user_id);

            transaction.commit();

            return user;

        } catch (jakarta.persistence.NoResultException e) {
            return null;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    public static String getUserLoginWithUserId(int user_id) {
        User user = getUserWithUserId(user_id);
        if (user == null) {
            return null;
        } else {
            return user.getLogin();
        }
    }

    public static String getUserPasswordWithUserId(int user_id) {
        User user = getUserWithUserId(user_id);
        if (user == null) {
            return null;
        } else {
            return user.getPassword();
        }
    }

    public static Set<Integer> getUserFavouritesWithUserId(int user_id) {
        User user = getUserWithUserId(user_id);
        if (user == null) {
            return null;
        } else {
            String favourites = user.getFavourites();
            String[] trackIds = favourites.split("\\|");
            Set<Integer> setTrackIds = new HashSet<>();
            for (String str : trackIds) {
                setTrackIds.add(Integer.parseInt(str));
            }
            return setTrackIds;
        }
    }

    public static User deleteUserWithUserId(int user_id) {
        User user = getUserWithUserId(user_id);

        if (user == null) {
            return null;
        }

        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.remove(user);

            transaction.commit();

            return user;
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    public static int getUserIdWithLogin(String login) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            getUserIdWithLogin.setParameter("login", login);
            int user_id = (int)getUserIdWithLogin.getSingleResult();

            transaction.commit();

            return user_id;

        } catch (jakarta.persistence.NoResultException e) {
            return 0;
        } catch (NonUniqueResultException e) {
            return -1;
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    public static void addNewUserWithAllParams(String login, String password, String favourites) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            User user = new User();
            user.setLogin(login);
            user.setPassword(password);
            user.setFavourites(favourites);

            entityManager.merge(user);

            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    public static void addNewUserWithLoginPassword(String login, String password) {
        addNewUserWithAllParams(login, password, null);
    }

    public static void addNewUserWithLogin(String login) {
        addNewUserWithAllParams(login, null, null);
    }

    public static void addTrackToFavouritesWithUserId(int user_id, int track_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            User user = entityManager.getReference(User.class, user_id);
            String currentFavourites = user.getFavourites();

            String newFavourites;
            if (currentFavourites == null) {
                newFavourites = track_id + "|";
            } else {
                newFavourites = currentFavourites + track_id + "|";
            }

            user.setFavourites(newFavourites);

            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    public static Set<Track> getTracksWithTrackIds(Set<Integer> setOfTrackIds) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            getTracksWithTrackIds.setParameter("track_id_set", setOfTrackIds);
            Set<Track> set = new HashSet<>(getTracksWithTrackIds.getResultList());

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

    public static void addNewTrackWithAllParams(String name, String author, String genre, String mood,
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
    }

    public static void addNewTrackWithNameAuthorGenreMood(String name, String author, String genre, String mood) {
        addNewTrackWithAllParams(name, author, genre, mood, null, null);
    }

    public static void updateTrackAllParamsWithUpdatedTrack(int track_id, Track updated_track) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Track track = entityManager.getReference(Track.class, track_id);

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
    }

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

    public static void closeHandler() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
