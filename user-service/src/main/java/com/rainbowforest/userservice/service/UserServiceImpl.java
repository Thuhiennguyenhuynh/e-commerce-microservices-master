package com.rainbowforest.userservice.service;

import com.rainbowforest.userservice.entity.User;
import com.rainbowforest.userservice.entity.UserRole;
import com.rainbowforest.userservice.repository.UserRepository;
import com.rainbowforest.userservice.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public User getUserByName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User saveUser(User user) {
        user.setActive(1);
        UserRole role = userRoleRepository.findUserRoleByRoleName("ROLE_USER");
        user.setRole(role);
        return userRepository.save(user);
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

        if (user.getUserPassword() != null) {
            // FIX LỖI: Cần mã hóa mật khẩu trước khi update
            existing.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
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
}
