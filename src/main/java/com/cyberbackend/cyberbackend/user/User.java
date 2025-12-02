package com.cyberbackend.cyberbackend.user;

import com.cyberbackend.cyberbackend.enums.Role;
import com.cyberbackend.cyberbackend.internship.Internship;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String fullName;

    @NonNull
    @Column(unique = true)
    private String email;

    @NonNull
    @JsonIgnore
    private String password;

    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

    //If employer, phone and address should be provided too during registration.
    @NonNull
    private String phone;

    @NonNull
    private String address;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private List<Role> roles;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    private List<Internship> internships;



    @Transient  // This ensures the field is NOT stored in the database
    @JsonIgnore
    private String token;

}