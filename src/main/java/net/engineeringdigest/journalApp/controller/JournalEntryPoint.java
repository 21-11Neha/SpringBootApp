package net.engineeringdigest.journalApp.controller;

import io.micrometer.common.util.StringUtils;
import net.engineeringdigest.journalApp.entity.JournalEntity;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntityService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryPoint {
    @Autowired
    JournalEntityService journalEntityService;
    @Autowired
    UserService userService;
    @PostMapping("/{username}")
    public ResponseEntity<?> createEntry(@RequestBody JournalEntity entry, @PathVariable String username){
        ResponseEntity<?> responseEntity;
        try{
            journalEntityService.saveEntry(entry, username);
            responseEntity = new ResponseEntity<>(entry, HttpStatus.OK);

        }catch (Exception e){
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
    @GetMapping("/{username}")
    public ResponseEntity<?> getEntriesByUsername(@PathVariable String username){
        ResponseEntity<?> responseEntity;
        User userInDB = userService.getByUsername(username);
        if(null != userInDB){
            List<JournalEntity> entries = userInDB.getJournalEntities();
            responseEntity = new ResponseEntity<>(entries, HttpStatus.OK);
        }
        else responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return responseEntity;
    }
    @GetMapping("id/{Id}")
    public ResponseEntity<?> getById(@PathVariable ObjectId Id){
        return Optional.ofNullable(journalEntityService.getById(Id)).map (val -> new ResponseEntity<>(val, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("id/{id}/{username}")
    public ResponseEntity<?> deleteById(@PathVariable ObjectId id, @PathVariable String username){
        journalEntityService.deleteById(id, username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("id/{id}")
    public ResponseEntity<?> updateEntry(@PathVariable ObjectId id, @RequestBody JournalEntity newEntry){
        Optional<JournalEntity> old = journalEntityService.getById(id);
        old.ifPresent(oldData -> {
            oldData.setContent(!StringUtils.isEmpty(newEntry.getContent()) ? newEntry.getContent() : oldData.getContent());
            oldData.setTitle(!StringUtils.isEmpty(newEntry.getTitle()) ? newEntry.getTitle() : oldData.getTitle());
            journalEntityService.saveEntry(oldData);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
