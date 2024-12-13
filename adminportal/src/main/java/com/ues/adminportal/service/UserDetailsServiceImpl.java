package com.ues.adminportal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ues.adminportal.entity.User;
import com.ues.adminportal.repository.UserRepository;

@Service
public class UserDetailsServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    public User findUserByUsername(String username) {
        logger.debug("Attempting to find user by username: {}", username);

        User appUser = userRepository.findByUsername(username);
        if (appUser == null) {
            logger.error("User not found with username: {}", username);
            return null;
        }

        logger.debug("User found: {}", appUser.getUsername());
        logger.debug("User email: {}", appUser.getEmail());

        return appUser;
    }
}
