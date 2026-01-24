package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.PatientDemographic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientDemographicRepository extends JpaRepository<PatientDemographic, UUID> {
}
