package au.com.xtramile.mpiapi.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResultResponse {

    private String status;
    private UUID patientId;
    private UUID linkId;
    private UUID candidatePatientId;
    private Integer confidenceScore;
    private String message;
    private String matchedVia;

}
