package hse.elysium.serverspring.controller;

import hse.elysium.databaseInteractor.CommentService;
import hse.elysium.databaseInteractor.TrackService;
import hse.elysium.databaseInteractor.UserService;
import hse.elysium.entities.Comment;
import hse.elysium.serverspring.forms.CommentRequest;
import hse.elysium.serverspring.forms.CommentResponse;
import hse.elysium.serverspring.loaders.CommentLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@ComponentScan("hse.elysium")
@RequestMapping("/elysium/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final TrackService trackService;
    private final Map<Integer, CommentLoader> commentLoaders = new ConcurrentHashMap<>();
    private final UserService userService;

    private CommentResponse commentToResponse(int trackId, Comment comment) {
        return new CommentResponse(trackId,
                userService.getUserLoginWithUserId(comment.getUserId()),
                comment.getContent(),
                comment.getTime());
    }

    @PostMapping("/addComment")
    ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest commentForm, @RequestAttribute("UserId") Integer userId) {
        int commentId = commentService.addNewCommentWithAllParams(userId, commentForm.getContent(), commentForm.getTrackId());
        Comment comment = commentService.getCommentWithCommentId(commentId);
        if (!commentLoaders.containsKey(userId)) {
            commentLoaders.put(userId, new CommentLoader(commentService));
        }
        commentLoaders.get(userId).updateCache(commentForm.getTrackId(), comment);
        return new ResponseEntity<>(commentToResponse(commentForm.getTrackId(), comment), HttpStatus.OK);
    }

    @GetMapping("/loadAllComments")
    ResponseEntity<List<CommentResponse>> loadAllComments(@RequestParam int trackId, @RequestAttribute("UserId") Integer userId) {
        if (!commentLoaders.containsKey(userId)) {
            commentLoaders.put(userId, new CommentLoader(commentService));
        }
        List<Comment> comments = commentLoaders.get(userId).loadAllComments(trackId);
        if (comments == null) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        List<CommentResponse> commentResponses = comments.stream().map(comment -> commentToResponse(trackId, comment)).toList();
        return new ResponseEntity<>(commentResponses, HttpStatus.OK);
    }
}