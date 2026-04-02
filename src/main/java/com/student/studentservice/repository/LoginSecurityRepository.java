package com.student.studentservice.repository;

import com.student.studentservice.entity.LoginSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginSecurityRepository extends JpaRepository<LoginSecurity, Long> {
}
