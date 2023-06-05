package hse.elysium.serverspring.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    int trackId;
    int commentId;
    String username;
    String content;
    Timestamp time;
}
