package au.com.xtramile.mpiapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_source_record", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientSourceRecord {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "system_id")
    private SourceSystem sourceSystem;

    @Column(name = "external_patient_id")
    private String externalPatientId;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "raw_data")
    private String rawData;

}
