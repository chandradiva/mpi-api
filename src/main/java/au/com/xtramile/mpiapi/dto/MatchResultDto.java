package au.com.xtramile.mpiapi.dto;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultDto {

    private String decision;
    private Integer confidenceScore;
    private UUID matchedPatientId;
    private Map<String, Boolean> fieldMatches;

    @Override
    public String toString() {
        return String.format(
                "Decision: %s | Score: %d | PatientID: %s | Matches: %s",
                decision, confidenceScore, matchedPatientId, fieldMatches
        );
    }

}
