package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.SourceSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SourceSystemRepository extends JpaRepository<SourceSystem, UUID> {
}
