package com.cyberbackend.cyberbackend.internship;

import com.cyberbackend.cyberbackend.enums.Level;
import com.cyberbackend.cyberbackend.enums.Payment;
import com.cyberbackend.cyberbackend.enums.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    // Find all internships filtered by conditions
    @Query("""
        SELECT DISTINCT i FROM Internship i
        WHERE (:levels IS NULL OR i.level IN :levels)
        AND (:types IS NULL OR i.type IN :types)
        AND (:payments IS NULL OR i.payment IN :payments)
        AND (
            :search IS NULL
            OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    Page<Internship> findByFilters(
            @Param("levels") List<Level> levels,
            @Param("types") List<Type> types,
            @Param("payments") List<Payment> payments,
            @Param("search") String search,
            Pageable pageable
    );

    // Find all internships by employer
    List<Internship> findByUserId(Long employerId);

    // Find internships by employer with filters
    @Query("""
        SELECT DISTINCT i FROM Internship i
        WHERE i.user.id = :employerId
        AND (:levels IS NULL OR i.level IN :levels)
        AND (:types IS NULL OR i.type IN :types)
        AND (:payments IS NULL OR i.payment IN :payments)
        AND (
            :search IS NULL
            OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))
        )
    """)
    Page<Internship> findByEmployerAndFilters(
            @Param("employerId") Long employerId,
            @Param("levels") List<Level> levels,
            @Param("types") List<Type> types,
            @Param("payments") List<Payment> payments,
            @Param("search") String search,
            Pageable pageable
    );

    // Find internship by id and employer
    Optional<Internship> findByIdAndUserId(Long internshipId, Long employerId);
}
