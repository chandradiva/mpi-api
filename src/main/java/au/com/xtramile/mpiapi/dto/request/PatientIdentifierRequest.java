package au.com.xtramile.mpiapi.dto.request;

public record PatientIdentifierRequest(
        String type,
        String value,
        String issuingAuthority,
        boolean verified
) {
}
