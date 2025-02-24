package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.JournalEntity;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntityService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
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
    @PostMapping("/create-journal")
    public ResponseEntity<?> createEntry(@RequestBody JournalEntity entry){
        ResponseEntity<?> responseEntity;
        try{
            journalEntityService.saveEntry(entry, SecurityContextHolder.getContext().getAuthentication().getName());
            responseEntity = new ResponseEntity<>(entry, HttpStatus.OK);

        }catch (Exception e){
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
    @GetMapping
    public ResponseEntity<?> getEntriesByUsername(){
        ResponseEntity<?> responseEntity;
        User userInDB = userService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if(null != userInDB){
            List<JournalEntity> entries = userInDB.getJournalEntities();
            responseEntity = new ResponseEntity<>(entries, HttpStatus.OK);
        }
        else responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return responseEntity;
    }
    @GetMapping("id/{Id}")
    public ResponseEntity<?> getById(@PathVariable ObjectId Id){
        User user = userService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<JournalEntity> list = user.getJournalEntities().stream().filter(val -> val.getId().equals(Id)).toList();
        if(!list.isEmpty()){
            return new ResponseEntity<>(list.get(0), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("id/{id}")
    public ResponseEntity<?> deleteById(@PathVariable ObjectId id){
        User user = userService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<JournalEntity> list = user.getJournalEntities().stream().filter(val -> val.getId().equals(id)).toList();
        if(!list.isEmpty()){
            journalEntityService.deleteById(id, user.getUsername());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("id/{id}")
    public ResponseEntity<?> updateEntry(@PathVariable ObjectId id, @RequestBody JournalEntity newEntry){
        User user = userService.getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        List<JournalEntity> list = user.getJournalEntities().stream().filter(val -> val.getId().equals(id)).toList();
        if(!list.isEmpty()){
            Optional<JournalEntity> old = journalEntityService.getById(id);
            old.ifPresent(oldData -> {
                oldData.setContent(!StringUtils.isEmpty(newEntry.getContent()) ? newEntry.getContent() : oldData.getContent());
                oldData.setTitle(!StringUtils.isEmpty(newEntry.getTitle()) ? newEntry.getTitle() : oldData.getTitle());
                journalEntityService.saveEntry(oldData);
            });
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
