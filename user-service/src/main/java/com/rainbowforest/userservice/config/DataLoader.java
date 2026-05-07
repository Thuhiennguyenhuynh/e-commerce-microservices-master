package com.rainbowforest.userservice.config;

import com.rainbowforest.userservice.entity.User;
import com.rainbowforest.userservice.entity.UserDetails;
import com.rainbowforest.userservice.entity.UserRole;
import com.rainbowforest.userservice.repository.UserRepository;
import com.rainbowforest.userservice.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Tạo Role ADMIN nếu chưa có
        UserRole adminRole = userRoleRepository.findUserRoleByRoleName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new UserRole();
            adminRole.setRoleName("ROLE_ADMIN");
            adminRole = userRoleRepository.save(adminRole); // Lưu lại để lấy ID
        }

        // // 2. Tạo Role USER nếu chưa có
        // UserRole userRole = userRoleRepository.findUserRoleByRoleName("ROLE_USER");
        // if (userRole == null) {
        //     userRole = new UserRole();
        //     userRole.setRoleName("ROLE_USER");
        //     userRoleRepository.save(userRole);
        // }

        // 3. Tạo tài khoản Admin mặc định
        // Kiểm tra xem user "admin" đã tồn tại chưa
        User admin = userRepository.findByUserName("admin");
        if (admin == null) {
            admin = new User();
            admin.setUserName("admin"); // Username riêng biệt
            admin.setUserPassword(passwordEncoder.encode("admin123")); 
            admin.setActive(1);
            admin.setRole(adminRole); // Cấp quyền Admin

            // Khởi tạo UserDetails với email admin@gmail.com
            // Do firstName và lastName có nullable = false nên bắt buộc phải gán giá trị
            UserDetails adminDetails = new UserDetails();
            adminDetails.setFirstName("Hệ thống");
            adminDetails.setLastName("Admin");
            adminDetails.setEmail("admin@gmail.com");

            // Liên kết Details vào User
            admin.setUserDetails(adminDetails);

            userRepository.save(admin);
            System.out.println("Tài khoản Admin đã được tạo thành công!");
        }
    }
}