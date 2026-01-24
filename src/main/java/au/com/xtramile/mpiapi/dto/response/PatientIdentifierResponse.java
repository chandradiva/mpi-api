package au.com.xtramile.mpiapi.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientIdentifierResponse {

    private UUID id;
    private String type;
    private String value;

}
