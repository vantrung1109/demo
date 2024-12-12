package digi.kitplay.data.model.api.response;

import androidx.room.Dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTest {
    Long userId;
    Long id;
    String title;
    String body;
}
