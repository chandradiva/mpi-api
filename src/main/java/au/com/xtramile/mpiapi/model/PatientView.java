package au.com.xtramile.mpiapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_view", schema = "public")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatientView {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "suburb")
    private String suburb;

    @Column(name = "state")
    private String state;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    private String country;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "link_status")
    private String linkStatus;

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @Column(name = "field_matches")
    private String fieldMatches;

    @Column(name = "external_patient_id")
    private String externalPatientId;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "system_id")
    private UUID systemId;

    @Column(name = "system_code")
    private String systemCode;

    @Column(name = "system_name")
    private String systemName;

}
