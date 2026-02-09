package au.com.xtramile.mpiapi.repository;

import au.com.xtramile.mpiapi.model.PatientView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PatientViewRepository extends JpaRepository<PatientView, UUID> {

    @Query("""
            SELECT pv FROM PatientView pv
            LEFT JOIN PatientIdentifier pi ON pv.id = pi.patientId
            WHERE
                pv.active = true AND
                (:linkStatus IS NULL OR pv.linkStatus = :linkStatus) AND
                (
                    :keyword IS NULL OR
                    pv.firstName LIKE %:keyword% OR
                    pv.lastName LIKE %:keyword% OR
                    LOWER(pi.identifierValue) LIKE %:keyword%
                )
            """)
    Page<PatientView> getListPageable(
            @Param("keyword") String keyword,
            @Param("linkStatus") String linkStatus,
            Pageable pageable);

}
