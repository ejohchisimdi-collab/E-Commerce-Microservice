package com.chisimdi.user.service.runnsers;

import com.chisimdi.user.service.models.User;
import com.chisimdi.user.service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MerchantRunner implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createAdmin();
    }

    public void createAdmin() {
        List<User> users = userRepository.findByRoles("Merchant");
        if (users.isEmpty()) {
            User user = new User();
            user.setRoles("Merchant");
            user.setPassword(bCryptPasswordEncoder.encode("Admin"));
            user.setUserName("Merchant");
            user.setEmail("Admin@gmail.com");
            user.setApproved(true);

            user.setName("Ifeanyi");
            userRepository.save(user);
        }
    }
}
