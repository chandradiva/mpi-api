package au.com.xtramile.mpiapi.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal mpiScore;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String status;

    private UUID recordId;
    private String hospitalId;
    private String hospitalName;
    private String recordNumber;
    private String matchStatus;

    private UUID demographicId;
    private String firstName;
    private String lastName;
    private String firstNameNorm;
    private String lastNameNorm;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private String gender;
    private String phone;
    private String phoneNorm;
    private String email;
    private String emailNorm;
    private String address;
    private String suburb;
    private String state;
    private String postcode;

    private List<PatientIdentifierResponse> identifiers;

}
