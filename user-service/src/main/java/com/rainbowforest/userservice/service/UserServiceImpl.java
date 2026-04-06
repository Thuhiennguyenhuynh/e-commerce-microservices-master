package com.rainbowforest.userservice.service;

import com.rainbowforest.userservice.entity.User;
import com.rainbowforest.userservice.entity.UserRole;
import com.rainbowforest.userservice.repository.UserRepository;
import com.rainbowforest.userservice.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User getUserByName(String userName) {
        return userRepository.findByUserName(userName);
    }

    // @Override
    // public User saveUser(User user) {
    //     user.setActive(1);
    //     UserRole role = userRoleRepository.findUserRoleByRoleName("ROLE_USER");
    //     user.setRole(role);
    //     return userRepository.save(user);
    // }

@Override
    public User saveUser(User user) {
        user.setActive(1);
        
        String roleName = "ROLE_USER"; // Mặc định là ROLE_USER
        
        // Nếu lúc tạo có gửi kèm roleName (ví dụ đăng ký tài khoản Admin)
        if(user.getRole() != null && user.getRole().getRoleName() != null) {
            roleName = user.getRole().getRoleName();
        }

        // Tìm role trong DB
        UserRole role = userRoleRepository.findUserRoleByRoleName(roleName);
        
        // Nếu role chưa tồn tại trong DB, tạo mới role đó để tránh bị null
        if (role == null) {
            role = new UserRole();
            role.setRoleName(roleName);
            role = userRoleRepository.save(role); 
        }

        user.setRole(role);
        // return userRepository.save(user);
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        if (user.getUserName() != null) {
            existing.setUserName(user.getUserName());
        }
        if (user.getUserPassword() != null) {
            existing.setUserPassword(user.getUserPassword());
        }

        // assume payload intentionally sets active (0 or 1)
        existing.setActive(user.getActive());

        if (user.getUserDetails() != null) {
            existing.setUserDetails(user.getUserDetails());
        }

        if (user.getRole() != null && user.getRole().getRoleName() != null) {
            UserRole role = userRoleRepository.findUserRoleByRoleName(user.getRole().getRoleName());
            if (role != null) {
                existing.setRole(role);
            }
        }

        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
        logger.info("User deleted with id: {}", id);
    }
}
