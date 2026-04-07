package com.rainbowforest.userservice.controller;

import com.rainbowforest.userservice.entity.User;
import com.rainbowforest.userservice.http.header.HeaderGenerator;
import com.rainbowforest.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
// @CrossOrigin("*")
@RestController
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private HeaderGenerator headerGenerator;
    
    @GetMapping (value = "/users")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users =  userService.getAllUsers();
        if(!users.isEmpty()) {
        	return new ResponseEntity<>(users, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
        }
        logger.warn("No users found");
        return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    @GetMapping (value = "/users", params = "name")
    public ResponseEntity<User> getUserByName(@RequestParam("name") String userName){
    	User user = userService.getUserByName(userName);
    	if(user != null) {
    		return new ResponseEntity<>(user, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
    	}
        logger.warn("User not found with name: {}", userName);
        return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    @GetMapping (value = "/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id){
        User user = userService.getUserById(id);
        if(user != null) {
    		return new ResponseEntity<>(user, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
    	}
        logger.warn("User not found with id: {}", id);
        return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    @PostMapping (value = "/users")
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request){
    	if(user != null)
    		try {
    			userService.saveUser(user);
    			logger.info("User created successfully with id: {}", user.getId());
    			return new ResponseEntity<>(user, headerGenerator.getHeadersForSuccessPostMethod(request, user.getId()), HttpStatus.CREATED);
    		}catch (Exception e) {
    			logger.error("Error creating user: ", e);
    			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

   @PostMapping(value = "/users/login")
public ResponseEntity<User> loginUser(@RequestBody Map<String, String> loginData) {
    String userName = loginData.get("userName");
    String password = loginData.get("password");
    
    User user = userService.getUserByName(userName);
    
    if (user != null && password != null && password.equals(user.getUserPassword())) {
        return new ResponseEntity<>(
                user,
                headerGenerator.getHeadersForSuccessGetMethod(),
                HttpStatus.OK);
    }
    return new ResponseEntity<>(
            headerGenerator.getHeadersForError(),
            HttpStatus.UNAUTHORIZED); // 401: Sai tài khoản/mật khẩu
}

    @PutMapping(value = "/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        if (updated != null) {
            logger.info("User updated successfully with id: {}", id);
            return new ResponseEntity<>(updated, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
        }
        logger.warn("User not found for update with id: {}", id);
        return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
            logger.info("User deleted successfully with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error deleting user with id: {}", id, e);
            return new ResponseEntity<>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
        }
    }
}
