package com.cyberbackend.cyberbackend.internship;

import com.cyberbackend.cyberbackend.dto.responses.PageResponseDTO;
import com.cyberbackend.cyberbackend.enums.Level;
import com.cyberbackend.cyberbackend.enums.Payment;
import com.cyberbackend.cyberbackend.enums.Type;
import com.cyberbackend.cyberbackend.exceptions.BadArgumentException;
import com.cyberbackend.cyberbackend.exceptions.CustomIOException;
import com.cyberbackend.cyberbackend.exceptions.ResourceNotFoundException;
import com.cyberbackend.cyberbackend.internship.image.Image; // <- adjust if your Image class package differs
import com.cyberbackend.cyberbackend.user.User;
import com.cyberbackend.cyberbackend.utils.AuthUtils;
import com.cyberbackend.cyberbackend.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class InternshipService {

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private AuthUtils authUtils;

    //--------------------------------------
    // PUBLIC: Anyone can view internships
    //--------------------------------------
    public PageResponseDTO<Internship> getAllInternshipsWithFilters(
            List<Level> levels,
            List<Type> types,
            List<Payment> payments,
            String search,
            int page,
            int size
    ) {
        Page<Internship> internships = internshipRepository.findByFilters(
                levels,
                types,
                payments,
                search,
                PaginationUtils.createPageable(page, size)
        );

        return PaginationUtils.getPageResponse(internships);
    }

    public Internship getSingleInternship(Long internshipId) {
        return internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found."));
    }

//Employer
    public PageResponseDTO<Internship> getAllInternshipsByEmployerWithFilters(
            List<Level> levels,
            List<Type> types,
            List<Payment> payments,
            String search,
            int page,
            int size
    ) {
        Long employerId = authUtils.getAuthUser().getId();

        Page<Internship> internships = internshipRepository.findByEmployerAndFilters(
                employerId,
                levels,
                types,
                payments,
                search,
                PaginationUtils.createPageable(page, size)
        );

        return PaginationUtils.getPageResponse(internships);
    }

    public Internship getSingleInternshipByEmployer(Long internshipId) {
        Long employerId = authUtils.getAuthUser().getId();

        return internshipRepository.findByIdAndUserId(internshipId, employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found or you do not own this internship."));
    }

    //--------------------------------------
    // CRUD: Create Internship (with images)
    //--------------------------------------
    @Transactional
    public Internship createInternship(InternshipDTO dto, User employer) throws CustomIOException {
        try {
            Internship internship = new Internship();
            internship.setTitle(dto.getTitle());
            internship.setDescription(dto.getDescription());
            internship.setLevel(dto.getLevel());
            internship.setType(dto.getType());
            internship.setPayment(dto.getPayment());
            internship.setUser(employer);

            // handle images if provided
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                List<Image> images = new ArrayList<>();
                for (MultipartFile file : dto.getImages()) {
                    if (file == null || file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
                        continue;
                    }
                    Image image = new Image();
                    image.setImageName(file.getOriginalFilename());
                    image.setImageType(file.getContentType());
                    image.setImageData(file.getBytes());
                    image.setInternship(internship);
                    images.add(image);
                }
                if (!images.isEmpty()) {
                    internship.setImages(images);
                }
            }

            return internshipRepository.save(internship);
        } catch (IOException e) {
            throw new CustomIOException("File error occurred while adding internship.", e);
        }
    }

    //--------------------------------------
    // CRUD: Update Internship (with images)
    //--------------------------------------
    @Transactional
    public Internship updateInternship(InternshipDTO dto, Internship internship) throws CustomIOException {
        try {
            if (dto.getTitle() != null)
                internship.setTitle(dto.getTitle());

            if (dto.getDescription() != null)
                internship.setDescription(dto.getDescription());

            if (dto.getLevel() != null)
                internship.setLevel(dto.getLevel());

            if (dto.getType() != null)
                internship.setType(dto.getType());

            if (dto.getPayment() != null)
                internship.setPayment(dto.getPayment());


            // handle images add-on (keeps existing images and appends new ones)
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                List<Image> existingImages = internship.getImages();
                if (existingImages == null) {
                    existingImages = new ArrayList<>();
                }
                for (MultipartFile file : dto.getImages()) {
                    if (file == null || file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
                        continue;
                    }
                    Image image = new Image();
                    image.setImageName(file.getOriginalFilename());
                    image.setImageType(file.getContentType());
                    image.setImageData(file.getBytes());
                    image.setInternship(internship);
                    existingImages.add(image);
                }
                if (!existingImages.isEmpty()) {
                    internship.setImages(existingImages);
                }
            }

            return internshipRepository.save(internship);
        } catch (IOException e) {
            throw new CustomIOException("File error occurred while updating internship.", e);
        }
    }

//delete internship
    public void deleteInternship(Long internshipId) {
        Long employerId = authUtils.getAuthUser().getId();

        internshipRepository.findByIdAndUserId(internshipId, employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found or you do not own this internship."));

        internshipRepository.deleteById(internshipId);
    }

    public Internship saveInternship(Internship internship) {
        return internshipRepository.save(internship);
    }
}
