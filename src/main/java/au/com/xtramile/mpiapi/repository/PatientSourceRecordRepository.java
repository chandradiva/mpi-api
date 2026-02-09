package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.PatientSourceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientSourceRecordRepository extends JpaRepository<PatientSourceRecord, UUID> {
}
