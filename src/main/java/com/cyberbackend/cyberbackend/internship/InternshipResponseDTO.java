package com.cyberbackend.cyberbackend.internship;


import com.cyberbackend.cyberbackend.enums.Level;
import com.cyberbackend.cyberbackend.enums.Payment;
import com.cyberbackend.cyberbackend.enums.Type;
import com.cyberbackend.cyberbackend.internship.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InternshipResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Type type;
    private Level level;
    private Payment payment;
    private Date listingDate;

    private List<Image> images;
    private String employerFullName;
}
