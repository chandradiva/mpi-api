package au.com.xtramile.mpiapi.service;

import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.dto.response.ProcessingResultResponse;

public interface MPIService {

    ProcessingResultResponse processingIncomingPatient(PatientRequest request);

}
