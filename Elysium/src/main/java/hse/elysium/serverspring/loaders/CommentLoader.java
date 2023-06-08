package hse.elysium.serverspring.loaders;

import hse.elysium.databaseInteractor.CommentService;
import hse.elysium.databaseInteractor.TrackService;
import hse.elysium.entities.Comment;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

@Configurable
@ComponentScan("hse.elysium")
@AllArgsConstructor
public class CommentLoader {

    private CommentService commentService;
    private TrackService trackService;
    private final Map<Integer, Integer> amountLoaded = new ConcurrentHashMap<>();
    private final Map<Integer, List<Integer>> loadedCommentIdListForTrack = new ConcurrentHashMap<>();
    private final List<Integer> cachedComments = new CopyOnWriteArrayList<>();
    private static final int AMOUNT_OF_CACHED_COMMENTS = 5;
    private static final int AMOUNT_OF_LOADED_COMMENTS_PER_REQUEST = 10;

    public List<Comment> loadAllComments(int track_id) {
        return commentService.getCommentsWithTrackId(track_id);
    }

}