package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.PatientIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatientIdentifierRepository extends JpaRepository<PatientIdentifier, UUID> {

    List<PatientIdentifier> findByRecordId(UUID recordId);

}
