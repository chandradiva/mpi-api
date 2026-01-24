package au.com.xtramile.mpiapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patient_demographic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDemographic {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "record_id")
    private UUID recordId;

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

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "phone_norm", length = 20)
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
