package com.cyberbackend.cyberbackend.internship;

import com.cyberbackend.cyberbackend.config.GlobalResponseHandler;
import com.cyberbackend.cyberbackend.dto.responses.PageResponseDTO;
import com.cyberbackend.cyberbackend.enums.Level;
import com.cyberbackend.cyberbackend.enums.Payment;
import com.cyberbackend.cyberbackend.enums.Type;
import com.cyberbackend.cyberbackend.user.User;
import com.cyberbackend.cyberbackend.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employers/internships")
@NoArgsConstructor
@AllArgsConstructor
public class InternshipEmployerController {
    @Autowired
    private InternshipService internshipService;

    @Autowired
    private GlobalResponseHandler responseHandler;

    @Autowired
    private AuthUtils authUtils;

    @Transactional
    @PostMapping
    public ResponseEntity<?> registerInternship(@Valid @ModelAttribute InternshipDTO dto) {
        try {
            User authUser = authUtils.getAuthUser();
            return responseHandler.wrapResponse(
                    internshipService.createInternship(dto, authUser),
                    "Internship added successfully.",
                    true,
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return responseHandler.wrapResponse("Error occurred while adding internship.", false, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllInternshipsByEmployers(
            @RequestParam(required = false) List<Type> types,
            @RequestParam(required = false) List<Level> levels,
            @RequestParam(required = false) List<Payment> payments,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        PageResponseDTO<Internship> internships = internshipService.getAllInternshipsByEmployerWithFilters(levels, types, payments, search, page, size);

        return responseHandler.wrapResponse(internships, "Internships available.", true, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getSingleInternshipByEmployer(@PathVariable Long id) {
        Internship internship = internshipService.getSingleInternshipByEmployer(id);
        return responseHandler.wrapResponse(internship, "Internship available.", true, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteSingleInternship(@PathVariable Long id) {
        internshipService.deleteInternship(id);
        return responseHandler.wrapResponse("Internship deleted successfully.", true, HttpStatus.OK);
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateSingleInternship(
            @PathVariable Long id,
            @Valid @ModelAttribute InternshipDTO dto
    ) {
        try {
            Internship internship = internshipService.getSingleInternshipByEmployer(id);
            return responseHandler.wrapResponse(
                    internshipService.updateInternship(dto, internship),
                    "Internship updated successfully.",
                    true,
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return responseHandler.wrapResponse("Error occurred while updating internship.", false, HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("{internshipId}/remove-images/{imageId}")
    public ResponseEntity<?> removeImagesFromInternship(
            @PathVariable Long internshipId,
            @PathVariable Long imageId
    ) {
        Internship internship = internshipService.getSingleInternshipByEmployer(internshipId);

        boolean removed = internship.getImages().removeIf(image -> image.getId().equals(imageId));

        if (!removed) {
            return responseHandler.wrapResponse(
                    "Internship does not contain the image.",
                    false,
                    HttpStatus.NOT_FOUND
            );
        }

        internshipService.saveInternship(internship);
        return responseHandler.wrapResponse(
                internship,
                "Image removed from the internship successfully.",
                true,
                HttpStatus.OK
        );
    }

}
