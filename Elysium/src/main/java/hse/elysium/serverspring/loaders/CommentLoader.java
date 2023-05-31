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

    private List<Comment> loadFromCached(int track_id) {
        int loaded = amountLoaded.get(track_id);
        List<Integer> comments = loadedCommentIdListForTrack.get(track_id);
        if (loaded == comments.size()) {
            return null;
        }
        int toLoad = Math.min(AMOUNT_OF_LOADED_COMMENTS_PER_REQUEST, comments.size() - loaded);
        List<Comment> res = commentService.getCommentsWithCommentIds(
                IntStream.rangeClosed(loaded, loaded + toLoad - 1)
                        .boxed()
                        .map(comments::get)
                        .toList());
        amountLoaded.put(track_id, loaded + toLoad);
        return res;
    }

    private void addToCache(int track_id) {
        if (cachedComments.size() == AMOUNT_OF_CACHED_COMMENTS) {
            int deleted = cachedComments.get(0);
            cachedComments.remove(0);
            amountLoaded.remove(deleted);
            loadedCommentIdListForTrack.remove(deleted);
        }
        cachedComments.add(track_id);
        amountLoaded.put(track_id, 0);
        List<Integer> commentList = trackService.getTrackCommentsWithTrackId(track_id);
        loadedCommentIdListForTrack.put(track_id, commentList);
    }

    public List<Comment> loadNextComments(int track_id) {
        if (!cachedComments.contains(track_id)) {
            addToCache(track_id);
        }
        return loadFromCached(track_id);
    }

    public List<Comment> loadAllComments(int track_id) {
        List<Integer> commentIds = trackService.getTrackCommentsWithTrackId(track_id);
        if (commentIds == null) return null;
        return commentService.getCommentsWithCommentIds(commentIds);
    }

}
