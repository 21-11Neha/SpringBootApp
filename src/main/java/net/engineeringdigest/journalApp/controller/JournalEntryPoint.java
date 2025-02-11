package net.engineeringdigest.journalApp.controller;

import io.micrometer.common.util.StringUtils;
import net.engineeringdigest.journalApp.entity.JournalEntity;
import net.engineeringdigest.journalApp.service.JournalEntityService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal/")
public class JournalEntryPoint {
    @Autowired
    JournalEntityService journalEntityService;
    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntity entry){
        ResponseEntity<?> responseEntity;
        try{
            entry.setDate(LocalDateTime.now());
            journalEntityService.saveEntry(entry);
            responseEntity = new ResponseEntity<>(entry, HttpStatus.OK);

        }catch (Exception e){
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
    @GetMapping
    public ResponseEntity<?> getEntries(){
        List<JournalEntity> entries = journalEntityService.getAll();
        ResponseEntity<?> responseEntity;
        if(!CollectionUtils.isEmpty(entries)){
            responseEntity = new ResponseEntity<>(entries, HttpStatus.OK);
        }
        else responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return responseEntity;
    }
    @GetMapping("id/{Id}")
    public ResponseEntity<?> getById(@PathVariable ObjectId Id){
        return Optional.ofNullable(journalEntityService.getById(Id)).map (val -> new ResponseEntity<>(val, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("id/{id}")
    public ResponseEntity<?> deleteById(@PathVariable ObjectId id){
        journalEntityService.deleteById(id);
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
