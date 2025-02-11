package net.engineeringdigest.journalApp.entity;


import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Data
@Document(collection = "journalEntries")
public class JournalEntity {
    @Id
    ObjectId id;
    String title;
    String content;
    LocalDateTime date;
}
