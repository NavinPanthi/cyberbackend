package com.cyberbackend.cyberbackend.user;

import com.cyberbackend.cyberbackend.config.GlobalResponseHandler;
import com.cyberbackend.cyberbackend.enums.Role;
import com.cyberbackend.cyberbackend.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/users")
@NoArgsConstructor
@AllArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private GlobalResponseHandler responseHandler;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthUtils authUtils;

    @GetMapping("{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        try {
            User authUser = authUtils.getAuthUser();
            Optional<User> user = userService.getById(userId);
            if (user.isEmpty()) {
                return responseHandler.wrapResponse("User not found.", false, HttpStatus.NOT_FOUND);
            }
            if (!authUser.getId().equals(userId) && authUser.getRoles().stream().noneMatch(role -> role == Role.admin)) {
                return responseHandler.wrapResponse("Unauthorized access.", false, HttpStatus.FORBIDDEN);
            }
            return responseHandler.wrapResponse(user, "Operation succeeded.", true, HttpStatus.OK);
        } catch (Exception e) {
            return responseHandler.wrapResponse("An unexpected error occurred.", false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping
    public ResponseEntity<?> updateUserByEmail(
            @Valid @ModelAttribute UserUpdateDTO dto) {
        try {
            User authUser = authUtils.getAuthUser();
            User updatedUser = userService.saveUser(authUser.getEmail(), dto);
            return responseHandler.wrapResponse(updatedUser, "User updated successfully.", true, HttpStatus.OK);

        } catch (Exception e) {
            return responseHandler.wrapResponse(e.getMessage(), false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}


