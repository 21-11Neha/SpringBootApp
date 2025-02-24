package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import net.engineeringdigest.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
//    @GetMapping
//    public ResponseEntity<?> getAllUsers(){
//        ResponseEntity<?> responseEntity;
//        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
//    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User userInDb = userService.getByUsername(username);
        if(null != userInDb){
            userInDb.setUsername(user.getUsername());
            userInDb.setPassword(user.getPassword());
            userService.saveUsers(userInDb);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping
    public ResponseEntity<?> deleteUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.deleteByUsername(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
