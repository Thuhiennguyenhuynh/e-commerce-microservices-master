package com.rainbowforest.userservice.controller;

import com.rainbowforest.userservice.entity.User;
import com.rainbowforest.userservice.http.header.HeaderGenerator;
import com.rainbowforest.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import javax.servlet.http.HttpServletRequest;
@CrossOrigin("*")
@RestController
public class RegisterController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private HeaderGenerator headerGenerator;
    
//     @PostMapping(value = "/registration")
//     public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request){
//     	if(user != null)
//     		try {
//     			userService.saveUser(user);
//     			return new ResponseEntity<User>(
//     					user,
//     					headerGenerator.getHeadersForSuccessPostMethod(request, user.getId()),
//     					HttpStatus.CREATED);
//     		}catch (Exception e) {
//     			e.printStackTrace();
//     			return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
// 		}
//     	return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
//     }
// }

@PostMapping(value = "/registration")
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request){
    	if(user != null) {
    		try {
                // Hứng object trả về sau khi lưu thành công
    			User savedUser = userService.saveUser(user); 
    			return new ResponseEntity<User>(
    					savedUser,
    					headerGenerator.getHeadersForSuccessPostMethod(request, savedUser.getId()),
    					HttpStatus.CREATED);
    		} catch (Exception e) {
    			e.printStackTrace(); // In lỗi ra console
    			return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
		    }
        }
    	return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
    }
}