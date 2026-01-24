package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRecordRepository extends JpaRepository<PatientRecord, UUID> {
}
