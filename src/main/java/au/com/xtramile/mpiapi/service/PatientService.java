package au.com.xtramile.mpiapi.service;

import au.com.xtramile.mpiapi.dto.request.PaginationRequest;
import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.dto.response.PaginatedResponse;
import au.com.xtramile.mpiapi.dto.response.PatientResponse;
import au.com.xtramile.mpiapi.model.*;

import java.util.List;
import java.util.UUID;

public interface PatientService {

    PatientResponse saveData(PatientRequest request);

    PatientResponse updateData(UUID id, PatientRequest request);

    PaginatedResponse<PatientResponse> getListPagination(PaginationRequest pageRequest, String keyword, String status);

    PatientResponse transform(Patient patient, PatientDemographic patientDemographic, PatientRecord patientRecord, List<PatientIdentifier> patientIdentifiers);

    PatientResponse transformView(PatientView patientView, List<PatientIdentifier> patientIdentifiers);

}
