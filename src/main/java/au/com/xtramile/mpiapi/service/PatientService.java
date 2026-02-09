package au.com.xtramile.mpiapi.service;

import au.com.xtramile.mpiapi.dto.request.PaginationRequest;
import au.com.xtramile.mpiapi.dto.request.PatientIdentifierRequest;
import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.dto.response.PaginatedResponse;
import au.com.xtramile.mpiapi.dto.response.PatientResponse;
import au.com.xtramile.mpiapi.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface PatientService {

    Optional<Patient> findExactMatchByStrongIdentifier(PatientRequest request);

    boolean isStrongIdentifier(String type);

    List<Patient> findCandidatesByDemographics(PatientRequest request);

    Patient findById(UUID id);

    void updatePatientIfNeeded(Patient existing, PatientRequest incoming);

    void addIdentifiers(Patient patient, List<PatientIdentifierRequest> identifiers);

    void createSourceRecord(Patient patient, PatientRequest incoming, SourceSystem sourceSystem);

    PatientLink createPatientLink(Patient source, Patient target, String status, int score, Map<String, Boolean> fieldMatches);

    Patient createNewPatient(PatientRequest request);

    PaginatedResponse<PatientResponse> getListPagination(PaginationRequest pageRequest, String keyword, String status);

    void deletePatient(UUID id);

    PatientResponse getDetailPatient(UUID id);

    void updatePatient(UUID id, PatientRequest request);

}
