package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

}
