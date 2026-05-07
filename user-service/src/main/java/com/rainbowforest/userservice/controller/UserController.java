package com.rainbowforest.userservice.controller;

import com.rainbowforest.userservice.entity.User;
import com.rainbowforest.userservice.http.header.HeaderGenerator;
import com.rainbowforest.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

// @CrossOrigin("*")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private HeaderGenerator headerGenerator;

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (!users.isEmpty()) {
            return new ResponseEntity<List<User>>(
                    users,
                    headerGenerator.getHeadersForSuccessGetMethod(),
                    HttpStatus.OK);
        }
        return new ResponseEntity<List<User>>(
                headerGenerator.getHeadersForError(),
                HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/users", params = "name")
    public ResponseEntity<User> getUserByName(@RequestParam("name") String userName) {
        User user = userService.getUserByName(userName);
        if (user != null) {
            return new ResponseEntity<User>(user, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
        }
        return new ResponseEntity<User>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return new ResponseEntity<User>(user, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
        }
        return new ResponseEntity<User>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/users")
    public ResponseEntity<User> addUser(@RequestBody User user, HttpServletRequest request) {
        if (user != null) {
            try {
                String encodedPassword = passwordEncoder.encode(user.getUserPassword());
                user.setUserPassword(encodedPassword);
                userService.saveUser(user);
                return new ResponseEntity<User>(
                        user,
                        headerGenerator.getHeadersForSuccessPostMethod(request, user.getId()),
                        HttpStatus.CREATED);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
    }

    // =========================================================================
    // 1. API ĐĂNG NHẬP CHO KHÁCH HÀNG (USER)
    // =========================================================================
    @PostMapping(value = "/users/login")
    public ResponseEntity<User> loginUser(@RequestBody Map<String, String> loginData) {
        String userName = loginData.get("userName");
        String password = loginData.get("password");

        // Tìm bằng Email trước, nếu không có thì tìm bằng Username
        User user = userService.getUserByEmail(userName);
        if (user == null) {
            user = userService.getUserByName(userName);
        }

        // Kiểm tra mật khẩu & Bắt buộc phải là ROLE_USER
        if (user != null && passwordEncoder.matches(password, user.getUserPassword())) {
            if (user.getRole() != null && "ROLE_USER".equals(user.getRole().getRoleName())) {
                return new ResponseEntity<User>(user, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<User>(headerGenerator.getHeadersForError(), HttpStatus.UNAUTHORIZED);
    }

    // =========================================================================
    // 2. API ĐĂNG NHẬP DÀNH RIÊNG CHO QUẢN TRỊ VIÊN (ADMIN)
    // =========================================================================
    @PostMapping(value = "/users/admin/login")
    public ResponseEntity<User> loginAdmin(@RequestBody Map<String, String> loginData) {
        System.out.println("DEBUG: Nhận được dữ liệu: " + loginData); 
        String email = loginData.get("email");
        String password = loginData.get("password");
        User user = userService.getUserByEmail(email);
        if (user == null) {
            user = userService.getUserByName(email);
        }
        if (user != null && passwordEncoder.matches(password, user.getUserPassword())) {
            String roleName = (user.getRole() != null) ? user.getRole().getRoleName() : "NULL";
            if (roleName != null && roleName.trim().equals("ROLE_ADMIN")) {
                return new ResponseEntity<User>(user, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<User>(headerGenerator.getHeadersForError(), HttpStatus.UNAUTHORIZED);
    }

    @PutMapping(value = "/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        if (updated != null) {
            return new ResponseEntity<User>(updated, headerGenerator.getHeadersForSuccessGetMethod(), HttpStatus.OK);
        }
        return new ResponseEntity<User>(headerGenerator.getHeadersForError(), HttpStatus.NOT_FOUND);
    }
}