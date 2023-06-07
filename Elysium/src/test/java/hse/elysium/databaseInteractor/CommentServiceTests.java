package hse.elysium.databaseInteractor;

import hse.elysium.entities.Comment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CommentServiceTests {
    @Test
    public void SimpleTest() {
        TrackService ts = new TrackService();
        List<Comment> res = ts.getTrackCommentsWithTrackId(14);
        for (Comment c : res) {
            System.out.println(c.getCommentId());
        }
    }
}