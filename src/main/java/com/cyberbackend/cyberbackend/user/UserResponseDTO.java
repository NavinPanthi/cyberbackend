package com.cyberbackend.cyberbackend.user;

import com.cyberbackend.cyberbackend.enums.Role;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String imageName;
    private String imageType;
    private byte[] imageData;
    private List<Role> roles;
    private String token;
}
