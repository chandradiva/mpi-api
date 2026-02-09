package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    @Query("""
            SELECT p FROM Patient p
            WHERE
                LOWER(p.lastName) LIKE CONCAT(LOWER(:lastName), '%') AND
                LOWER(p.firstName) LIKE CONCAT(LOWER(:firstName), '%') AND
                p.dob BETWEEN :dobStart AND :dobEnd AND
                p.active = true
            """)
    List<Patient> findCandidates(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("dobStart") LocalDate dobStart,
            @Param("dobEnd") LocalDate dobEnd);

}
