package com.coworking.backend.config;

import com.coworking.backend.model.User;
import com.coworking.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin user 
        if (userRepository.findByEmail("nidhalgharbi5@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("nidhal.gharbi");
            admin.setEmail("nidhalgharbi5@gmail.com");
            admin.setPassword(passwordEncoder.encode("Nidhal@admin"));
            admin.setType(User.UserType.ADMIN);
            admin.setEnabled(true);
            admin.setFirstName("Nidhal");
            admin.setLastName("Gharbi");
            admin.setPhone("99078443");
            admin.setProfileImagePath("/uploads/admin.png");
            userRepository.save(admin);
        }

        // Create receptionist user 
        if (userRepository.findByEmail("akermiayoub20@gmail.com").isEmpty()) {
            User receptionist = new User();
            receptionist.setUsername("ayoub.akermi");
            receptionist.setEmail("akermiayoub20@gmail.com");
            receptionist.setPassword(passwordEncoder.encode("Ayoub@receptionist"));
            receptionist.setType(User.UserType.RECEPTIONISTE);
            receptionist.setEnabled(true);
            receptionist.setFirstName("Ayoub");
            receptionist.setLastName("Akermi");
            receptionist.setPhone("23666875");
            receptionist.setProfileImagePath("/uploads/receptionist.png");
            userRepository.save(receptionist);
        }
    }
}