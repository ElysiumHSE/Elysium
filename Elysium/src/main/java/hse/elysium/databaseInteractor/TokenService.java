package hse.elysium.databaseInteractor;

import jakarta.persistence.*;

import hse.elysium.entities.Token;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TokenService {
    private final EntityManagerFactory
        entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private final EntityManager entityManager = entityManagerFactory.createEntityManager();

    private final Query getTokenIdWithTokenValueQuery = entityManager.createNativeQuery
        ("SELECT token_id from Token where Token.token_value=:token_value");

    @SuppressWarnings("unchecked")
    private final TypedQuery<String> searchRecordsWithRevokedOrExpiredQuery =
        (TypedQuery<String>)entityManager.createNativeQuery
        ("SELECT token_value from Token where Token.revoked=TRUE OR Token.expired=TRUE");

    @SuppressWarnings("unchecked")
    private final TypedQuery<String> setRevokedForUserChangedPasswordWithUserIdQuery =
        (TypedQuery<String>)entityManager.createNativeQuery
        ("SELECT token_value from Token where Token.user_id=:user_id", String.class);

    /**
     * Given a token_value, finds token_id of matching record in Token database table.
     * @return token_id of corresponding token, if matching record was found.
     * @throws jakarta.persistence.NoResultException, if matching record was not found.
     */
    public int getTokenIdWithTokenValue(String token_value) {
        EntityTransaction transaction = entityManager.getTransaction();

        int token_id;

        try {
            transaction.begin();

            getTokenIdWithTokenValueQuery.setParameter("token_value", token_value);
            token_id = (int) getTokenIdWithTokenValueQuery.getSingleResult();

            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return token_id;
    }

    /**
     * Given a token_value, finds matching record in Token database table.
     * @return Object of class Token, if matching record was found.
     * @throws jakarta.persistence.NoResultException, if matching record was not found.
     */
    public Token getTokenWithTokenValue(String token_value) {
        int token_id = getTokenIdWithTokenValue(token_value);

        EntityTransaction transaction = entityManager.getTransaction();

        Token token;

        try {
            transaction.begin();

            token = entityManager.find(Token.class, token_id);

            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return token;
    }

    /**
     * Given token_value, revoked flag, expired flag and user_id of a new token, creates a new
     * corresponding record in Token database table.
     * @return token_id of new token, if adding new record to Token database table was successful.
     * @throws jakarta.persistence.PersistenceException, if token with corresponding token_value already exists.
     */
    public int addNewTokenWithAllParams(String token_value, boolean revoked, boolean expired, int user_id) {
        int token_id;
        try {
            token_id = getTokenIdWithTokenValue(token_value);
        } catch (jakarta.persistence.NoResultException e) {
            token_id = -1;
        }

        if (token_id != -1) {
            throw new jakarta.persistence.PersistenceException
                    ("token with corresponding token_value already exists");
        }

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Token token = new Token();
        token.setTokenValue(token_value);
        token.setRevoked(revoked);
        token.setExpired(expired);
        token.setUserId(user_id);

        entityManager.merge(token);

        transaction.commit();

        token_id = getTokenIdWithTokenValue(token_value);
        return token_id;
    }

    /**
     * Given token_value and user_id of a new token, creates a new corresponding record in Token database table.
     * Revoked and expired flags are set false.
     * @return token_id of new token, if adding new record to Token database table was successful,
     * @throws jakarta.persistence.PersistenceException, if token with corresponding token_value already exists.
     */
    public int addNewTokenWithTokenValueUserId(String token_value, int user_id) {
        return addNewTokenWithAllParams(token_value, false, false, user_id);
    }

    /**
     * Given token_value, sets revoked flag in a corresponding record in Token database table.
     * @return true, if revoked flag was set successfully, and false, if revoked flag was already set true.
     * @throws jakarta.persistence.NoResultException, if corresponding record in Token database table was not found.
     */
    public boolean setRevokedWithTokenValue(String token_value) {
        int token_id = getTokenIdWithTokenValue(token_value);

        boolean result;

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Token token = entityManager.getReference(Token.class, token_id);

        result = !token.getRevoked();

        token.setRevoked(true);

        transaction.commit();

        return result;
    }

    /**
     * Given token_value, sets expired flag in a corresponding record in Token database table.
     * @return true, if expired flag was set successfully, and false, if expired flag was already set true.
     * @throws jakarta.persistence.NoResultException, if corresponding record in Token database table was not found.
     */
    public boolean setExpiredWithTokenValue(String token_value) {
        int token_id = getTokenIdWithTokenValue(token_value);

        boolean result;

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Token token = entityManager.getReference(Token.class, token_id);

        result = !token.getExpired();

        token.setExpired(true);

        transaction.commit();

        return result;
    }

    /**
     * Given user_id, sets revoked flags in all corresponding records in Token database table.
     * @throws jakarta.persistence.NoResultException, if no corresponding records in Token database table were found.
     */
    public void setRevokedForUserChangedPasswordWithUserId(int user_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        ArrayList<String> array;

        try {
            transaction.begin();

            setRevokedForUserChangedPasswordWithUserIdQuery.setParameter("user_id", user_id);
            array = new ArrayList<>(setRevokedForUserChangedPasswordWithUserIdQuery.getResultList());

            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }

        for (String token_value : array) {
            setRevokedWithTokenValue(token_value);
        }
    }

    /**
     * Given token_value, deletes matching record of Token database table.
     * @return Object of class Token representing the deleted token, if matching record was found.
     * @throws jakarta.persistence.NoResultException, if matching record was not found.
     */
    public Token deleteTokenWithTokenValue(String token_value) {
        Token token = getTokenWithTokenValue(token_value);

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        entityManager.remove(token);

        transaction.commit();

        return token;
    }

    /**
     * Deletes records with revoked or expired flags set in Token database table.
     */
    public void deleteRecordsWithRevokedOrExpired() {
        EntityTransaction transaction = entityManager.getTransaction();

        ArrayList<String> array;

        try {
            transaction.begin();

            array = new ArrayList<>(searchRecordsWithRevokedOrExpiredQuery.getResultList());

            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }

        for (String token_value : array) {
            deleteTokenWithTokenValue(token_value);
        }
    }

    /**
     * Close entity manager and entity manager factory when finished working with class.
     */
    public void closeHandler() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
