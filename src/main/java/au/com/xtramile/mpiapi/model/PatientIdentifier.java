package au.com.xtramile.mpiapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_identifier", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientIdentifier {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "patient_id", insertable = false, updatable = false)
    private UUID patientId;

    @Column(name = "identifier_type")
    private String identifierType;

    @Column(name = "identifier_value")
    private String identifierValue;

    @Column(name = "issuing_authority")
    private String issuingAuthority;

    @Column(name = "is_verified")
    private boolean verified;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
