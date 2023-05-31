package hse.elysium.serverspring.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    int trackId;
    int commentId;
    String username;
    String content;
}
