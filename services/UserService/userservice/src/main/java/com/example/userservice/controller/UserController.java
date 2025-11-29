package com.example.userservice.controller;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(
        origins = {
                "http://localhost:4200",
                "http://k8s-todo-todofron-629d1e1f26-57fbf8f5c030f005.elb.eu-central-1.amazonaws.com"
        },
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PATCH,
                RequestMethod.DELETE,
                RequestMethod.OPTIONS
        },
        allowedHeaders = {"Content-Type", "Authorization"}
)
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
      UserDTO foundedUser = userService.findById(id);
      return new ResponseEntity<>(foundedUser,HttpStatus.OK);
    }


    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody User user){
        UserDTO createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody User req) {
        UserDTO user = userService.login(req);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable int id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();

    }

    @PatchMapping("users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable int id, @RequestBody User user) {
        UserDTO updatedUser = userService.updateUser(id, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }


}
