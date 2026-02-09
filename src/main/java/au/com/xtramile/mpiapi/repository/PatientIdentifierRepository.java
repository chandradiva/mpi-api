package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.PatientIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientIdentifierRepository extends JpaRepository<PatientIdentifier, UUID> {

    Optional<PatientIdentifier> findByIdentifierTypeAndIdentifierValue(String identifierType, String identifierValue);

    List<PatientIdentifier> findByPatientId(UUID patientId);

}
