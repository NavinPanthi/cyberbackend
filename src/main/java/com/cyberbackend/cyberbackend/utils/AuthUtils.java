package com.cyberbackend.cyberbackend.utils;


import com.cyberbackend.cyberbackend.enums.Role;
import com.cyberbackend.cyberbackend.user.User;
import com.cyberbackend.cyberbackend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthUtils {
    @Autowired
    private UserService userService;


    private User getAuthenticatedUser() {
        return userService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public User getAuthUser() {
        return getAuthenticatedUser();
    }

    public List<Role> getAuthRoles() {
        return getAuthenticatedUser().getRoles();
    }
}
