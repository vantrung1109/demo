package digi.kitplay.data.model.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentTest {
    Long postId;
    Long id;
    String name;
    String email;
    String body;
}
