package digi.kitplay.data.model.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(tableName = "db_actions")
public class ActionEntity extends BaseEntity{
    @PrimaryKey
    @NonNull
    private Long id;
    private String description;
    private Integer status;
    private Long timestamp;
    private String type;
}

