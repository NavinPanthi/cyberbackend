package com.cyberbackend.cyberbackend.internship.image;


import com.cyberbackend.cyberbackend.internship.Internship;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] imageData;

    private String imageName;

    private String imageType;

    @OneToOne
    @JoinColumn(name = "internship_id", nullable = false)
    @JsonBackReference
    private Internship internship;
}
