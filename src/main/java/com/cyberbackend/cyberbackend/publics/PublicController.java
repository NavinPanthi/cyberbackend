package com.cyberbackend.cyberbackend.publics;

import com.cyberbackend.cyberbackend.config.GlobalResponseHandler;
import com.cyberbackend.cyberbackend.exceptions.EmailAlreadyExistsException;
import com.cyberbackend.cyberbackend.exceptions.ResourceNotFoundException;
import com.cyberbackend.cyberbackend.jwt.JWTService;
import com.cyberbackend.cyberbackend.user.User;
import com.cyberbackend.cyberbackend.user.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/public")
@NoArgsConstructor
@AllArgsConstructor
public class PublicController {
    @Autowired
    private UserService userService;

    @Autowired
    private GlobalResponseHandler responseHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@Valid @ModelAttribute UserRegistrationDTO dto) {
        try {
            User savedUser = userService.registerNewUser(dto);
            return responseHandler.wrapResponse(savedUser, "User registered successfully.", true, HttpStatus.CREATED);
        } catch (EmailAlreadyExistsException e) {
            return responseHandler.wrapResponse("Email already exists", false, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return responseHandler.wrapResponse(e.getMessage(), false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request) {
        try {
            String email = request.getEmail();
            String password = request.getPassword();
            User user = userService.findByUserName(email);
            if (user == null){
                throw new ResourceNotFoundException("User with email not found.");
            }
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return responseHandler.wrapResponse("Password is incorrect.", false, HttpStatus.BAD_REQUEST);
            }
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            if(authentication.isAuthenticated()){
                user.setToken(jwtService.generateToken(email));
            }
            return responseHandler.wrapResponse(userService.getUserResponse(user), "Logged in successfully.", true, HttpStatus.OK);
        } catch (Exception e) {
            return responseHandler.wrapResponse(e.getMessage(), false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
