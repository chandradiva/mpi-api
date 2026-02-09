package au.com.xtramile.mpiapi.service;

import au.com.xtramile.mpiapi.dto.MatchResultDto;
import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.model.Patient;

import java.util.List;

public interface PatientMatchingService {

    MatchResultDto comparePatients(PatientRequest incoming, Patient existing);

    MatchResultDto findMatch(PatientRequest request, List<Patient> existingPatients);

}
