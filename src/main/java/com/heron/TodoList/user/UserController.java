package com.heron.TodoList.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity CreateUser(@RequestBody UserModel user){
        UserModel userModel = this.userRepository.findByUsername(user.getUsername());
        if(userModel != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("usuário já existe!");
        }

        var passwordHash = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
        user.setPassword(passwordHash);

        UserModel userCreated = this.userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }
}
