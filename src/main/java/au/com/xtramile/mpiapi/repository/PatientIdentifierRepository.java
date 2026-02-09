package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.PatientIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientIdentifierRepository extends JpaRepository<PatientIdentifier, UUID> {

    Optional<PatientIdentifier> findByIdentifierTypeAndIdentifierValue(String identifierType, String identifierValue);

    List<PatientIdentifier> findByPatientId(UUID patientId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PatientIdentifier WHERE patientId = :patientId")
    void deleteByPatientId(@Param("patientId") UUID patientId);

}
