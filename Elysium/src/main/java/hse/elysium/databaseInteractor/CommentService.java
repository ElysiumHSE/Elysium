package hse.elysium.databaseInteractor;

import hse.elysium.entities.Comment;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    private final EntityManagerFactory
            entityManagerFactory = Persistence.createEntityManagerFactory("default");
    private final EntityManager entityManager = entityManagerFactory.createEntityManager();

    private final Query getMaxCommentId = entityManager.createNativeQuery
            ("SELECT MAX(comment_id) from Comment");

    @SuppressWarnings("unchecked")
    private final TypedQuery<Comment> getCommentsWithCommentIdsQuery =
            (TypedQuery<Comment>)entityManager.createNativeQuery
                    ("SELECT * FROM Comment where track_id = :track_id", Comment.class);

    /**
     * Given a comment_id, finds matching record in Comment database table.
     * @return Object of class Comment, if matching record was found, and null otherwise.
     */
    public Comment getCommentWithCommentId(int comment_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        Comment comment;

        try {
            transaction.begin();

            comment = entityManager.find(Comment.class, comment_id);

            transaction.commit();

        } catch (NoResultException | IllegalArgumentException e) {
            return null;

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return comment;
    }

    /**
     * Given a track_id, finds matching records with corresponding track_id's
     * in Comment database table.
     * @return List of Comment objects, if at least one record with given track_id was found,
     * and null, if no matches were found.
     */
    public List<Comment> getCommentsWithTrackId(int track_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        ArrayList<Comment> array;

        try {
            transaction.begin();

            getCommentsWithCommentIdsQuery.setParameter("track_id", track_id);
            array = new ArrayList<>(getCommentsWithCommentIdsQuery.getResultList());

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
     * Given user_id and content, adds a new record with given parameters to Comment database table.
     * @return comment_id of new record
     */
    public synchronized int addNewCommentWithAllParams(int user_id, String content, int track_id) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Comment comment = new Comment();
            comment.setUserId(user_id);
            comment.setContent(content);
            comment.setTime(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Europe/Moscow"))));
            comment.setTrackId(track_id);

            entityManager.merge(comment);

            transaction.commit();

        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
        return (int)getMaxCommentId.getSingleResult();
    }

    /**
     * Close entity manager and entity manager factory when finished working with class.
     */
    public synchronized void closeHandler() {
        entityManager.close();
        entityManagerFactory.close();
    }
}
