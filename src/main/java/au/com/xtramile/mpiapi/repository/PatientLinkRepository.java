package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.PatientLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientLinkRepository extends JpaRepository<PatientLink, UUID> {
}
