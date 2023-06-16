package hse.elysium.serverspring.loaders;

import hse.elysium.databaseInteractor.CommentService;
import hse.elysium.databaseInteractor.TrackService;
import hse.elysium.entities.Comment;
import hse.elysium.serverspring.auth.AuthenticationFilter;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Configurable
@ComponentScan("hse.elysium")
@AllArgsConstructor
public class CommentLoader {

    private final Logger log = LogManager.getLogger(AuthenticationFilter.class);
    private CommentService commentService;
    private final Map<Integer, List<Comment>> cached = new HashMap<>();
    private final List<Integer> cachedList = new ArrayList<>();
    private static final int AMOUNT_OF_CACHED_COMMENTS = 5;

    private synchronized void addToCache(int trackId, List<Comment> comments) {
        if (comments.isEmpty()) return;
        if (cachedList.size() == AMOUNT_OF_CACHED_COMMENTS) {
            cached.remove(cachedList.get(0));
            log.info("Removed from cache " + cachedList.get(0));
            cachedList.remove(0);
        }
        cachedList.add(trackId);
        cached.put(trackId, comments);
        log.info("Added to cache " + trackId);
    }

    private synchronized List<Comment> loadFromCache(int trackId) {
        if (!cachedList.contains(trackId)) return null;
        log.info("Loaded from cache " + trackId);
        return cached.get(trackId);
    }

    public synchronized void updateCache(int trackId, Comment comment) {
        if (cachedList.contains(trackId)) {
            cached.get(trackId).add(comment);
        }
    }

    public List<Comment> loadAllComments(int trackId) {
        List<Comment> comments = loadFromCache(trackId);
        if (comments != null) return comments;
        comments = commentService.getCommentsWithTrackId(trackId);
        addToCache(trackId, comments);
        return comments;
    }

}