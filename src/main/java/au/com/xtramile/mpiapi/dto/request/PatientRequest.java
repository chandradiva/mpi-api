package au.com.xtramile.mpiapi.dto.request;

import java.util.List;
import java.util.UUID;

public record PatientRequest(
        UUID id,
        String firstName,
        String lastName,
        String dob,
        String gender,
        String phone,
        String email,
        String address,
        String suburb,
        String state,
        String postcode,
        String hospitalId,
        String recordNumber,
        List<PatientIdentifierRequest> identifiers
) {
}
