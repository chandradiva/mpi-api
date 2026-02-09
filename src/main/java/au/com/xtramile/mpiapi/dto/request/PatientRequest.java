package au.com.xtramile.mpiapi.dto.request;

import java.util.List;
import java.util.UUID;

public record PatientRequest(
        UUID id,
        String firstName,
        String lastName,
        String dob,
        String gender,
        String phoneNo,
        String email,
        String address,
        String suburb,
        String state,
        String postalCode,
        String country,
        UUID sourceSystemId,
        String externalPatientId,
        List<PatientIdentifierRequest> identifiers
) {
}
