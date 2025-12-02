package com.cyberbackend.cyberbackend.internship;


import com.cyberbackend.cyberbackend.enums.Level;
import com.cyberbackend.cyberbackend.enums.Payment;
import com.cyberbackend.cyberbackend.enums.Type;
import com.cyberbackend.cyberbackend.internship.image.Image;
import com.cyberbackend.cyberbackend.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Type type;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Level level;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Payment payment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date listingDate = new Date();

    @ToString.Exclude
    @OneToOne(mappedBy = "internship", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Image> images;


    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    @JsonBackReference
    private User user;

}
