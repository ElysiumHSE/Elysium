package hse.elysium.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Comment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "comment_id")
    private int commentId;
    @Basic
    @Column(name = "user_id")
    private int userId;
    @Basic
    @Column(name = "content")
    private String content;
    @Basic
    @Column(name = "time")
    private Timestamp time;
    @Basic
    @Column(name = "track_id")
    private int trackId;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return commentId == comment.commentId && userId == comment.userId && trackId == comment.trackId
                && Objects.equals(content, comment.content)
                && Objects.equals(time, comment.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, userId, content, time, trackId);
    }
}
