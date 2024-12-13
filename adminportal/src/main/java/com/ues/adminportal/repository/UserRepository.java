package com.ues.adminportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ues.adminportal.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
