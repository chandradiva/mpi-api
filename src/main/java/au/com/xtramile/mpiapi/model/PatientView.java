package au.com.xtramile.mpiapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
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

    @Column(name = "mpi_score")
    private BigDecimal mpiScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status")
    private String status;

    // patient record
    @Column(name = "record_id")
    private UUID recordId;

    @Column(name = "hospital_id")
    private String hospitalId;

    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "record_number")
    private String recordNumber;

    @Column(name = "match_status")
    private String matchStatus;

    // patient demographic
    @Column(name = "demographic_id")
    private UUID demographicId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name_norm")
    private String firstNameNorm;

    @Column(name = "last_name_norm")
    private String lastNameNorm;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "phone_norm")
    private String phoneNorm;

    @Column(name = "email")
    private String email;

    @Column(name = "email_norm")
    private String emailNorm;

    @Column(name = "address")
    private String address;

    @Column(name = "suburb")
    private String suburb;

    @Column(name = "state")
    private String state;

    @Column(name = "postcode")
    private String postcode;

}
