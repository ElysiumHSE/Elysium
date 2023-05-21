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

    @PostMapping("/leaveComment")
    ResponseEntity<Integer> leaveComment(@RequestBody CommentRequest commentForm, @RequestAttribute("UserId") Integer user_id) {
        int commentId = commentService.addNewCommentWithAllParams(user_id, commentForm.getContent());
        trackService.addCommentToCommentsWithTrackId(commentForm.getTrackId(), commentId);
        return new ResponseEntity<>(commentId, HttpStatus.OK);
    }

    @GetMapping("/loadComments")
    ResponseEntity<List<CommentResponse>> loadComments(@RequestParam int track_id, @RequestAttribute("UserId") Integer user_id) {
        if (!commentLoaders.containsKey(user_id)) {
            commentLoaders.put(user_id, new CommentLoader(commentService, trackService));
        }
        List<Comment> comments = commentLoaders.get(user_id).loadNextComments(track_id);
        if (comments == null) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        List<CommentResponse> commentResponses = comments.stream().map(comment -> new CommentResponse(track_id,
                comment.getCommentId(),
                userService.getUserLoginWithUserId(comment.getUserId()),
                comment.getContent())).toList();
        return new ResponseEntity<>(commentResponses, HttpStatus.OK);
    }

}
