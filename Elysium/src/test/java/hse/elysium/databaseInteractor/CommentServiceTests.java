package hse.elysium.databaseInteractor;

import hse.elysium.entities.Comment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CommentServiceTests {
    @Test
    public void SimpleTest() {
        CommentService cs = new CommentService();
        int id = cs.addNewCommentWithAllParams(1, "Wow!!!11");

        TrackService ts = new TrackService();
        Assertions.assertNull(ts.getTrackCommentsWithTrackId(1));

        ts.addCommentToCommentsWithTrackId(1, id);
        List<Integer> res = ts.getTrackCommentsWithTrackId(1);
        Assertions.assertEquals(1, res.size());
        Assertions.assertEquals(id, res.get(0));

        Comment comment = cs.getCommentWithCommentId(id);
        List<Comment> c = cs.getCommentsWithCommentIds(res);
        Assertions.assertEquals(comment, c.get(0));
    }
}
