package au.com.xtramile.mpiapi.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {

    private UUID id;
    private String firstName;
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dob;

    private String gender;
    private String phoneNo;
    private String email;

    private String address;
    private String suburb;
    private String state;
    private String postalCode;
    private String country;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    private String linkStatus;
    private Integer confidenceScore;
    private String externalPatientId;
    private String systemId;
    private String systemCode;
    private String systemName;

    private List<PatientIdentifierResponse> identifiers;

}
