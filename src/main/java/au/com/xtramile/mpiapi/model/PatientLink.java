package au.com.xtramile.mpiapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_link", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientLink {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "source_patient_id")
    private Patient sourcePatient;

    @ManyToOne
    @JoinColumn(name = "target_patient_id")
    private Patient targetPatient;

    @Column(name = "link_status")
    private String linkStatus;

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @Column(name = "field_matches")
    private String fieldMatches;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reason")
    private String reason;

}
