package hse.elysium.databaseInteractor;

import jakarta.persistence.*;

import hse.elysium.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final EntityManagerFactory
            entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private final EntityManager entityManager = entityManagerFactory.createEntityManager();

    private final Query getUserIdWithLoginQuery = entityManager.createNativeQuery
            ("SELECT user_id from User where User.login=:login");

    /**
     * Given a user_id, finds matching record in User database table.
     * @return Object of class User, if matching record was found, and null otherwise.
     */
    public User getUserWithUserId(int user_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        User user;

        try {
            transaction.begin();

            user = entityManager.find(User.class, user_id);

            transaction.commit();

        } catch (NoResultException | IllegalArgumentException e) {
            return null;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return user;
    }

    /**
     * Given a user_id, finds login of corresponding user in a matching record of User database table.
     * @return String login, if matching record was found, and null otherwise.
     */
    public String getUserLoginWithUserId(int user_id) {
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
    public String getUserPasswordWithUserId(int user_id) {
        User user = getUserWithUserId(user_id);
        if (user == null) {
            return null;
        } else {
            return user.getPassword();
        }
    }

    /**
     * Given a user_id, finds favourites of corresponding user in a matching record of User database table.
     * @return List of Integers, if matching record was found, and null otherwise.
     */
    public List<Integer> getUserFavouritesWithUserId(int user_id) {
        User user = getUserWithUserId(user_id);
        if (user == null) {
            return null;
        } else {
            String favourites = user.getFavourites();
            String[] trackIds = favourites.split("\\|");
            ArrayList<Integer> arrayOfTrackIds = new ArrayList<>();
            for (String str : trackIds) {
                arrayOfTrackIds.add(Integer.parseInt(str));
            }
            return arrayOfTrackIds;
        }
    }

    /**
     * Given user_id and password, updates password in matching record of User database table.
     * @return true, if password was updated successfully, and false,
     * if user_id did not match any of User database table records or
     * given password String object is null.
     */
    public boolean changePasswordWithUserIdPassword(int user_id, String password) {
        if (password == null) {
            return false;
        }

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        User user = entityManager.getReference(User.class, user_id);
        if (user == null) {
            return false;
        }

        user.setPassword(password);

        transaction.commit();

        return true;
    }

    /**
     * Given a user_id, deletes a matching record in User database table.
     * @return Object of class User representing the deleted record, if matching record was found, and null otherwise.
     */
    public User deleteUserWithUserId(int user_id) {
        User user = getUserWithUserId(user_id);

        if (user == null) {
            return null;
        }

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        entityManager.remove(user);

        transaction.commit();

        return user;
    }

    /**
     * Given a login, finds user_id of matching record in User database table.
     * @return user_id of corresponding user, if matching record was found.
     * @throws jakarta.persistence.NoResultException, if matching record was not found.
     */
    public int getUserIdWithLogin(String login) throws NoResultException {
        EntityTransaction transaction = entityManager.getTransaction();

        int userId;

        try {
            transaction.begin();

            getUserIdWithLoginQuery.setParameter("login", login);
            userId = (int) getUserIdWithLoginQuery.getSingleResult();

            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return userId;
    }

    /**
     * Given login, password and favourites of a new user, creates a new corresponding record in User database table.
     * @return user_id of new user, if adding new record to User database table was successful.
     * @throws jakarta.persistence.PersistenceException, if user with matching login already exists.
     */
    public int addNewUserWithAllParams(String login, String password, String favourites)
            throws PersistenceException {

        int userId;
        try {
            userId = getUserIdWithLogin(login);
        } catch (jakarta.persistence.NoResultException e) {
            userId = -1;
        }

        if (userId != -1) {
            throw new jakarta.persistence.PersistenceException
                    ("user with corresponding login already exists");
        }

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setFavourites(favourites);

        entityManager.merge(user);

        transaction.commit();

        userId = getUserIdWithLogin(login);
        return userId;
    }

    /**
     * Given login and password of a new user, creates a new corresponding record in User database table.
     * @return user_id of new user, if adding new record to User database table was successful.
     * @throws jakarta.persistence.PersistenceException, if user with matching login already exists.
     */
    public int addNewUserWithLoginPassword(String login, String password) throws PersistenceException {
        return addNewUserWithAllParams(login, password, null);
    }

    /**
     * Given user_id and track_id, finds track with corresponding track_id in user's favourites
     * in a corresponding record in User database table.
     * @return index of favourites' string, if track with corresponding track_id was found.
     * @throws jakarta.persistence.NoResultException, if track was not found.
     * @throws jakarta.persistence.PersistenceException, if user was not found.
     */
    public int findTrackInUserFavourites(int user_id, int track_id) throws NoResultException, PersistenceException {
        User user = getUserWithUserId(user_id);
        if (user == null) {
            throw new jakarta.persistence.PersistenceException("user was not found");
        }

        String currentFavourites = user.getFavourites();

        if (currentFavourites == null) {
            throw new jakarta.persistence.NoResultException("track was not found");
        } else {
            int trackIdx = currentFavourites.indexOf("|" + track_id + "|");
            if (trackIdx == -1) {
                trackIdx = currentFavourites.indexOf(track_id + "|");
                if (trackIdx != 0) {
                    throw new jakarta.persistence.NoResultException("track was not found");
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
     * @return true, if track with corresponding track_id was added to favourites successfully,
     * and false, if track is already in favourites.
     * @throws jakarta.persistence.PersistenceException, if user_id is invalid.
     */
    public boolean addTrackToFavouritesWithUserId(int user_id, int track_id) throws PersistenceException {
        int trackIdx;
        try {
            trackIdx = findTrackInUserFavourites(user_id, track_id);
        } catch (jakarta.persistence.NoResultException e) {
            trackIdx = -1;
        }

        if (trackIdx >= 0) {
            return false;
        }

        EntityTransaction transaction = entityManager.getTransaction();

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

        return true;
    }

    /**
     * Given user_id and track_id, deletes track with corresponding track_id from user's favourites
     * in a corresponding record in User database table.
     * @return true, if track with corresponding track_id was deleted from favourites successfully,
     * and false, if track was not found in favourites.
     * @throws jakarta.persistence.PersistenceException, if user_id is invalid.
     */
    public boolean deleteTrackFromFavouritesWithUserId(int user_id, int track_id) throws PersistenceException {
        int trackIdx;
        try {
            trackIdx = findTrackInUserFavourites(user_id, track_id);
        } catch (jakarta.persistence.NoResultException e) {
            trackIdx = -1;
        }

        if (trackIdx == -1) {
            return false;
        }

        EntityTransaction transaction = entityManager.getTransaction();

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

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        int userId;
        try {
            userId = getUserIdWithLogin(username);
        } catch (jakarta.persistence.NoResultException e) {
            throw new UsernameNotFoundException("No such login");
        }

        User user = getUserWithUserId(userId);
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(), user.getPassword(), new ArrayList<>());
    }

    /**
     * Close entity manager and entity manager factory when finished working with class.
     */
    public void closeHandler() {
        entityManager.close();
        entityManagerFactory.close();
    }
}