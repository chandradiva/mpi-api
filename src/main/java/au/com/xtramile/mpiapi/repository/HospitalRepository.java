package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, String> {
}
