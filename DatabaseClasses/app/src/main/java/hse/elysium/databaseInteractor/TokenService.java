package hse.elysium.databaseInteractor;

import jakarta.persistence.*;

import hse.elysium.entities.Token;

import java.util.ArrayList;

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
     * @return token_id of corresponding token, if matching record was found, and -1, if matching record was not found.
     */
    public int getTokenIdWithTokenValue(String token_value) {
        EntityTransaction transaction = entityManager.getTransaction();

        int token_id;

        try {
            transaction.begin();

            getTokenIdWithTokenValueQuery.setParameter("token_value", token_value);
            token_id = (int) getTokenIdWithTokenValueQuery.getSingleResult();

            transaction.commit();

        } catch (jakarta.persistence.NoResultException e) {
            return -1;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return token_id;
    }

    /**
     * Given a token_value, finds matching record in Token database table.
     * @return Object of class Token, if matching record was found, and null otherwise.
     */
    public Token getTokenWithTokenValue(String token_value) {
        int token_id = getTokenIdWithTokenValue(token_value);
        if (token_id == -1) {
            return null;
        }

        EntityTransaction transaction = entityManager.getTransaction();

        Token token;

        try {
            transaction.begin();

            token = entityManager.find(Token.class, token_id);

            transaction.commit();

        } catch (NoResultException | IllegalArgumentException e) {
            return null;

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
     * @return token_id of new token, if adding new record to Token database table was successful,
     * and -1, if token with corresponding token_value already exists.
     */
    public int addNewTokenWithAllParams(String token_value, boolean revoked, boolean expired, int user_id) {
        int token_id = getTokenIdWithTokenValue(token_value);

        if (token_id != -1) {
            return -1;
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
     * and -1, if token with corresponding token_value already exists.
     */
    public int addNewTokenWithTokenValueUserId(String token_value, int user_id) {
        return addNewTokenWithAllParams(token_value, false, false, user_id);
    }

    /**
     * Given token_value, sets revoked flag in a corresponding record in Token database table.
     * @return 1, if revoked flag was set successfully, 0, if revoked flag was already set true, and -1, if
     * corresponding record in Token database table was not found.
     */
    public int setRevokedWithTokenValue(String token_value) {
        int token_id = getTokenIdWithTokenValue(token_value);

        if (token_id == -1) {
            return -1;
        }

        int result;

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Token token = entityManager.getReference(Token.class, token_id);

        if (token.getRevoked()) {
            result = 0;
        } else {
            result = 1;
        }

        token.setRevoked(true);

        transaction.commit();

        return result;
    }

    /**
     * Given token_value, sets expired flag in a corresponding record in Token database table.
     * @return 1, if expired flag was set successfully, 0, if expired flag was already set true, and -1, if
     * corresponding record in Token database table was not found.
     */
    public int setExpiredWithTokenValue(String token_value) {
        int token_id = getTokenIdWithTokenValue(token_value);

        if (token_id == -1) {
            return -1;
        }

        int result;

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Token token = entityManager.getReference(Token.class, token_id);

        if (token.getExpired()) {
            result = 0;
        } else {
            result = 1;
        }

        token.setExpired(true);

        transaction.commit();

        return result;
    }

    /**
     * Given user_id, sets revoked flags in all corresponding records in Token database table.
     * @return 1, if revoked flags were set successfully, and 0, if no
     * corresponding records in Token database table were found.
     */
    public int setRevokedForUserChangedPasswordWithUserId(int user_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        ArrayList<String> array;

        int result = 1;

        try {
            transaction.begin();

            setRevokedForUserChangedPasswordWithUserIdQuery.setParameter("user_id", user_id);
            array = new ArrayList<>(setRevokedForUserChangedPasswordWithUserIdQuery.getResultList());

            if (array.size() == 0) {
                result = 0;
            }

            transaction.commit();

        } catch (jakarta.persistence.NoResultException e) {
            return 0;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }

        if (result == 0) {
            return 0;
        }

        for (String token_value : array) {
            setRevokedWithTokenValue(token_value);
        }

        return 1;
    }

    /**
     * Given token_value, deletes matching record of Token database table.
     * @return Object of class Token representing the deleted record, if matching record was found, and null otherwise.
     */
    public Token deleteTokenWithTokenValue(String token_value) {
        Token token = getTokenWithTokenValue(token_value);

        if (token == null) {
            return null;
        }

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        entityManager.remove(token);

        transaction.commit();

        return token;
    }

    /**
     * Deletes records with revoked or expired flags set in Token database table.
     * @return 1, if corresponding records were deleted successfully, and 0, if no
     * corresponding records in Token database table were found.
     */
    public int deleteRecordsWithRevokedOrExpired() {
        EntityTransaction transaction = entityManager.getTransaction();

        ArrayList<String> array;

        int result = 1;

        try {
            transaction.begin();

            array = new ArrayList<>(searchRecordsWithRevokedOrExpiredQuery.getResultList());

            if (array.size() == 0) {
                result = 0;
            }

            transaction.commit();

        } catch (jakarta.persistence.NoResultException e) {
            return 0;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }

        if (result == 0) {
            return 0;
        }

        for (String token_value : array) {
            deleteTokenWithTokenValue(token_value);
        }

        return 1;
    }

    /**
     * Close entity manager and entity manager factory when finished working with class.
     */
    public void closeHandler() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
