package hse.elysium.databaseInteractor;

import jakarta.persistence.*;

import hse.elysium.entities.User;

import java.util.HashSet;
import java.util.Set;

public class UserService {
    private static final EntityManagerFactory
        entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private static final EntityManager entityManager = entityManagerFactory.createEntityManager();

    private static final Query getUserIdWithLoginQuery = entityManager.createNativeQuery
        ("SELECT user_id from User where User.login=:login");

    /**
     * Given a user_id, finds matching record in User database table.
     * @return Object of class User, if matching record was found, and null otherwise.
     */
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

    /**
     * Given a user_id, finds login of corresponding user in a matching record of User database table.
     * @return String login, if matching record was found, and null otherwise.
     */
    public static String getUserLoginWithUserId(int user_id) {
        User user = getUserWithUserId(user_id);
        if (user == null) {
            return null;
        } else {
            return user.getLogin();
        }
    }

    /**
     * Given a user_id, finds password of corresponding user in a matching record of User database table.
     * @return String password, if matching record was found, and null otherwise.
     */
    public static String getUserPasswordWithUserId(int user_id) {
        User user = getUserWithUserId(user_id);
        if (user == null) {
            return null;
        } else {
            return user.getPassword();
        }
    }

    /**
     * Given a user_id, finds favourites of corresponding user in a matching record of User database table.
     * @return Set of Integers, if matching record was found, and null otherwise.
     */
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

    /**
     * Given a user_id, deletes a matching record in User database table.
     * @return Object of class User representing the deleted record, if matching record was found, and null otherwise.
     */
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

    /**
     * Given a login, finds user_id of matching record in User database table.
     * @return user_id of corresponding user, if matching record was found, and -1, if matching record was not found.
     */
    public static int getUserIdWithLogin(String login) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            getUserIdWithLoginQuery.setParameter("login", login);
            int user_id = (int) getUserIdWithLoginQuery.getSingleResult();

            transaction.commit();

            return user_id;

        } catch (jakarta.persistence.NoResultException e) {
            return -1;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    /**
     * Given login, password and favourites of a new user, creates a new corresponding record in User database table.
     * @return user_id of new user, if adding new record to User database table was successful,
     * and -1, if user with corresponding login already exists.
     */
    public static int addNewUserWithAllParams(String login, String password, String favourites) {
        int user_id = getUserIdWithLogin(login);

        if (user_id != -1) {
            return -1;
        }

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

        user_id = getUserIdWithLogin(login);
        return user_id;
    }

    /**
     * Given login and password of a new user, creates a new corresponding record in User database table.
     * @return user_id of new user, if adding new record to User database table was successful,
     * and -1, if user with corresponding login already exists.
     */
    public static int addNewUserWithLoginPassword(String login, String password) {
        return addNewUserWithAllParams(login, password, null);
    }

    /**
     * Given user_id and track_id, finds track with corresponding track_id in user's favourites
     * in a corresponding record in User database table.
     * @return index of favourites' string, if track with corresponding track_id was found,
     * -1, if track was not found, and -2, if user was not found.
     */
    public static int findTrackInUserFavourites(int user_id, int track_id) {
        User user = getUserWithUserId(user_id);
        if (user == null) {
            return -2;
        }

        String currentFavourites = user.getFavourites();

        if (currentFavourites == null) {
            return -1;
        } else {
            int trackIdx = currentFavourites.indexOf("|" + track_id + "|");
            if (trackIdx == -1) {
                trackIdx = currentFavourites.indexOf(track_id + "|");
                if (trackIdx != 0) {
                    return -1;
                } else {
                    return trackIdx;
                }
            } else {
                return trackIdx;
            }
        }
    }

    /**
     * Given user_id and track_id, adds track with corresponding track_id to user's favourites
     * in a corresponding record in User database table.
     * @return 1, if track with corresponding track_id was added to favourites successfully,
     * 0, if track is already in favourites, and -1, if user_id is invalid.
     */
    public static int addTrackToFavouritesWithUserId(int user_id, int track_id) {
        int trackIdx = findTrackInUserFavourites(user_id, track_id);
        if (trackIdx == -2) {
            return -1;
        }
        if (trackIdx >= 0) {
            return 0;
        }
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
        return 1;
    }

    /**
     * Given user_id and track_id, deletes track with corresponding track_id from user's favourites
     * in a corresponding record in User database table.
     * @return 1, if track with corresponding track_id was deleted from favourites successfully,
     * 0, if track was not found in favourites, and -1, if user_id is invalid.
     */
    public static int deleteTrackFromFavouritesWithUserId(int user_id, int track_id) {
        int trackIdx = findTrackInUserFavourites(user_id, track_id);
        if (trackIdx == -2) {
            return -1;
        }
        if (trackIdx == -1) {
            return 0;
        }
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            User user = entityManager.getReference(User.class, user_id);
            String currentFavourites = user.getFavourites();

            String newFavourites;
            if (trackIdx == 0) {
                newFavourites = currentFavourites.replaceFirst(track_id + "\\|", "");
            } else {
                newFavourites = currentFavourites.replaceFirst("\\|" + track_id + "\\|", "|");
            }

            user.setFavourites(newFavourites);

            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return 1;
    }

    /**
     * Close entity manager and entity manager factory when finished working with class.
     */
    public static void closeHandler() {
        entityManager.close();
        entityManagerFactory.close();
    }
}