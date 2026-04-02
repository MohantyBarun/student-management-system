package com.student.studentservice.repository;

import com.student.studentservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User,Long>{
}
