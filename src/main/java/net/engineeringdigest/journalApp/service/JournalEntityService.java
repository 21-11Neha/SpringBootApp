package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.JournalEntity;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntityService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveEntry(JournalEntity journalEntity, String username){
        User user = userRepository.getByUsername(username);
        if(null != user){
            journalEntity.setDate(LocalDateTime.now());
            journalEntryRepository.save(journalEntity);
            user.getJournalEntities().add(journalEntity);
            userRepository.save(user);
        }
    }
    public void saveEntry(JournalEntity journalEntity){
        journalEntryRepository.save(journalEntity);
    }
    public List<JournalEntity> getAll(){
        return journalEntryRepository.findAll();
    }
    public Optional<JournalEntity> getById(ObjectId id){
        return journalEntryRepository.findById(id);
    }
    public void deleteById(ObjectId id, String username){
        User userInDb = userRepository.getByUsername(username);
        if(null != userInDb){
            userInDb.getJournalEntities().removeIf(val -> val.getId().equals(id));
            userRepository.save(userInDb);
        }
        journalEntryRepository.deleteById(id);
    }
}
