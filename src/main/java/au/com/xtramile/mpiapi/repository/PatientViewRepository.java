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
            LEFT JOIN PatientIdentifier pi ON pv.recordId = pi.recordId
            WHERE
                (:status IS NULL OR pv.status = :status) AND
                (
                    :keyword IS NULL OR
                    pv.firstNameNorm LIKE %:keyword% OR
                    pv.lastNameNorm LIKE %:keyword% OR
                    UPPER(pi.value) LIKE %:keyword%
                )
            """)
    Page<PatientView> getListPageable(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable);

}
