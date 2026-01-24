package au.com.xtramile.mpiapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "patient_identifier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientIdentifier {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "record_id")
    private UUID recordId;

    @Column(name = "type")
    private String type;

    @Column(name = "value")
    private String value;

}
