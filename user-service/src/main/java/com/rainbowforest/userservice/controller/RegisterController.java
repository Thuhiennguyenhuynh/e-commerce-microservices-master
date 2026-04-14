package com.rainbowforest.userservice.controller;

import com.rainbowforest.userservice.entity.User;
import com.rainbowforest.userservice.entity.UserRole;
import com.rainbowforest.userservice.http.header.HeaderGenerator;
import com.rainbowforest.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HeaderGenerator headerGenerator;

    @PostMapping(value = "/registration")
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request) {
        if (user != null)
    		try {
		// Mã hóa mật khẩu trước khi lưu xuống DB
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
            // Gán role mặc định cho user mới đăng ký (Giả sử ID 2 là USER)
            UserRole userRole = new UserRole();
            userRole.setId(2L);
            user.setRole(userRole);
            userService.saveUser(user);
            return new ResponseEntity<User>(
                    user,
                    headerGenerator.getHeadersForSuccessPostMethod(request, user.getId()),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
    }
}
