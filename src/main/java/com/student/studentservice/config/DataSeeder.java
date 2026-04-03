package com.student.studentservice.config;


import com.student.studentservice.entity.*;
import com.student.studentservice.repository.*;
import com.student.studentservice.service.AccountStatus;
import com.student.studentservice.service.AdminLevel;
import com.student.studentservice.service.Roles;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("dev")
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonsRepository personsRepository;
    private final AdminsRepository adminsRepository;
    private final LoginSecurityRepository loginSecurityRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (!personsRepository.existsByEmail("admin@college.com")) {

            Admins admin= new Admins();
            Persons persons=new Persons();
            persons.setName("Super Admin");
            persons.setEmail("admin@college.com");
            admin.setDepartment("Admin");
            admin.setAdminLevel(AdminLevel.SUPER_ADMIN);

            Persons savedPerson= personsRepository.save(persons);
            personsRepository.flush();
            admin.setPersons(savedPerson);
            adminsRepository.save(admin);

            User adminUser = new User();
            adminUser.setEmail("admin@college.com");
            adminUser.setPassword(passwordEncoder.encode("Admin@123"));
            adminUser.setRole(Roles.ROLE_ADMIN);
            adminUser.setIsActive(true);
            adminUser.setIsFirstTimeLogin(false);
            adminUser.setTempPassword(null);
            adminUser.setTempPasswordExpiry(null);
            adminUser.setTempPasswordAttemptsCount(0);
            adminUser.setIsEmailVerified(true);
            adminUser.setPersons(savedPerson);

            userRepository.save(adminUser);

            LoginSecurity loginSecurity = new LoginSecurity();
            loginSecurity.setAccountStatus(AccountStatus.ACTIVE);
            loginSecurity.setWrongPasswordCount(0);
            loginSecurity.setPermanentBlock(false);
            loginSecurity.setUser(adminUser);
            loginSecurityRepository.save(loginSecurity);

            log.info("Default admin created — email: {}", adminUser.getEmail());
        }
    }
}
