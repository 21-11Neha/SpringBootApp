package net.engineeringdigest.journalApp.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@Document(collection = "journalEntries")
public class JournalEntity {
    @Id
    ObjectId id;
    @NonNull
    String title;
    String content;
    LocalDateTime date;
}
