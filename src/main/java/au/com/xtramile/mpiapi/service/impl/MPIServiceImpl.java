package au.com.xtramile.mpiapi.service.impl;

import au.com.xtramile.mpiapi.dto.MatchResultDto;
import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.dto.response.ProcessingResultResponse;
import au.com.xtramile.mpiapi.model.Patient;
import au.com.xtramile.mpiapi.model.PatientLink;
import au.com.xtramile.mpiapi.model.SourceSystem;
import au.com.xtramile.mpiapi.repository.SourceSystemRepository;
import au.com.xtramile.mpiapi.service.MPIService;
import au.com.xtramile.mpiapi.service.PatientMatchingService;
import au.com.xtramile.mpiapi.service.PatientService;
import au.com.xtramile.mpiapi.util.MPIUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MPIServiceImpl implements MPIService {

    private final PatientService patientService;
    private final PatientMatchingService patientMatchingService;
    private final SourceSystemRepository sourceSystemRepo;

    @Transactional
    @Override
    public ProcessingResultResponse processingIncomingPatient(PatientRequest request) {
        SourceSystem sourceSystem = sourceSystemRepo.findById(request.sourceSystemId()).orElse(null);

        // step 1: normalize incoming data
        PatientRequest normalized = normalizeIncomingData(request);

        // step 2: search for exact match on strong identifier
        Optional<Patient> exactMatchPatient = patientService.findExactMatchByStrongIdentifier(normalized);
        if (exactMatchPatient.isPresent()) {
            return handleExactMatch(exactMatchPatient.get(), normalized, sourceSystem);
        }

        // step 3: search candidates by patient demographic
        List<Patient> patientCandidates = patientService.findCandidatesByDemographics(normalized);
        if (patientCandidates.isEmpty()) {
            return handleNoMatch(normalized, sourceSystem);
        }

        // step 4: run matching algorithm on candidates
        MatchResultDto bestMatch = patientMatchingService.findMatch(normalized, patientCandidates);

        // step 5: route based on match decision
        return routeBasedOnMatchDecision(bestMatch, normalized, sourceSystem);
    }

    private PatientRequest normalizeIncomingData(PatientRequest dto) {
        return new PatientRequest(null,
                MPIUtil.normalize(dto.firstName()),
                MPIUtil.normalize(dto.lastName()),
                dto.dob(),
                MPIUtil.normalize(dto.gender()),
                MPIUtil.normalizePhone(dto.phoneNo()),
                MPIUtil.normalizeEmail(dto.email()),
                MPIUtil.normalize(dto.address()),
                MPIUtil.normalize(dto.suburb()),
                MPIUtil.normalize(dto.state()),
                dto.postalCode(),
                MPIUtil.normalize(dto.country()),
                dto.sourceSystemId(),
                dto.externalPatientId(),
                dto.identifiers());
    }

    private ProcessingResultResponse routeBasedOnMatchDecision(
            MatchResultDto matchResult,
            PatientRequest request,
            SourceSystem sourceSystem
    ) {
        return switch (matchResult.getDecision()) {
            case "AUTO_MATCH" -> handleAutoMatch(matchResult, request, sourceSystem);
            case "REVIEW" -> handleReview(matchResult, request, sourceSystem);
            case "NO_MATCH" -> handleNoMatch(request, sourceSystem);
            default -> throw new IllegalStateException("Unknown match decision: " + matchResult.getDecision());
        };
    }

    private ProcessingResultResponse handleExactMatch(
            Patient existingPatient,
            PatientRequest request,
            SourceSystem sourceSystem
    ) {
        // 1. Update patient golden record if needed (merge better data)
        patientService.updatePatientIfNeeded(existingPatient, request);

        // 2. Add new identifiers (might have new phone/email from this hospital)
        patientService.addIdentifiers(existingPatient, request.identifiers());

        // 3. Create source record linking to existing patient
        patientService.createSourceRecord(existingPatient, request, sourceSystem);

        // 4. Create patient link with AUTO_MATCH status
        Map<String, Boolean> fieldMatches = new HashMap<>();
        fieldMatches.put("strong_identifier", true);

        patientService.createPatientLink(
                existingPatient,
                existingPatient,
                "AUTO_MATCH",
                100,
                fieldMatches);

        return ProcessingResultResponse.builder()
                .status("AUTO_MATCH")
                .patientId(existingPatient.getId())
                .confidenceScore(100)
                .matchedVia("STRONG_IDENTIFIER")
                .message("Patient matched via strong identifier (National_ID/SSN/Passport)")
                .build();
    }

    private ProcessingResultResponse handleAutoMatch(
            MatchResultDto matchResult,
            PatientRequest request,
            SourceSystem sourceSystem
    ) {
        Patient existingPatient = patientService.findById(matchResult.getMatchedPatientId());

        // 1. Update patient golden record if needed (merge better data)
        patientService.updatePatientIfNeeded(existingPatient, request);

        // 2. Add new identifiers
        patientService.addIdentifiers(existingPatient, request.identifiers());

        // 3. Create source record
        patientService.createSourceRecord(existingPatient, request, sourceSystem);

        // 4. Create patient link with AUTO_MATCH status
        patientService.createPatientLink(
                existingPatient,
                existingPatient,
                "AUTO_MATCH",
                matchResult.getConfidenceScore(),
                matchResult.getFieldMatches());

        return ProcessingResultResponse.builder()
                .status("AUTO_MATCH")
                .patientId(existingPatient.getId())
                .confidenceScore(matchResult.getConfidenceScore())
                .message("Patient automatically matched and linked")
                .matchedVia("DEMOGRAPHICS")
                .build();
    }

    private ProcessingResultResponse handleReview(
            MatchResultDto matchResult,
            PatientRequest request,
            SourceSystem sourceSystem
    ) {
        // 1. Create temporary patient record
        Patient tempPatient = patientService.createNewPatient(request);

        // 2. Add identifiers for temp patient
        patientService.addIdentifiers(tempPatient, request.identifiers());

        // 3. Create source record for temp patient
        patientService.createSourceRecord(tempPatient, request, sourceSystem);

        // 4. Create patient link with REVIEW status
        Patient matchedPatient = patientService.findById(matchResult.getMatchedPatientId());
        PatientLink link = patientService.createPatientLink(
                tempPatient, // source (new temp)
                matchedPatient, // target (potential match)
                "REVIEW",
                matchResult.getConfidenceScore(),
                matchResult.getFieldMatches());

        return ProcessingResultResponse.builder()
                .status("REVIEW")
                .patientId(tempPatient.getId())
                .linkId(link.getId())
                .candidatePatientId(matchResult.getMatchedPatientId())
                .confidenceScore(matchResult.getConfidenceScore())
                .message("Uncertain match - manual review required")
                .matchedVia("DEMOGRAPHICS")
                .build();
    }

    private ProcessingResultResponse handleNoMatch(PatientRequest request, SourceSystem sourceSystem) {
        // 1. Create new patient record
        Patient newPatient = patientService.createNewPatient(request);

        // 2. Add identifiers
        patientService.addIdentifiers(newPatient, request.identifiers());

        // 3. Create source record
        patientService.createSourceRecord(newPatient, request, sourceSystem);

        return ProcessingResultResponse.builder()
                .status("NO_MATCH")
                .patientId(newPatient.getId())
                .confidenceScore(0)
                .message("New patient created - no match found")
                .build();
    }

}
