package au.com.xtramile.mpiapi.service.impl;

import au.com.xtramile.mpiapi.dto.request.PaginationRequest;
import au.com.xtramile.mpiapi.dto.request.PatientIdentifierRequest;
import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.dto.response.PaginatedResponse;
import au.com.xtramile.mpiapi.dto.response.PatientIdentifierResponse;
import au.com.xtramile.mpiapi.dto.response.PatientResponse;
import au.com.xtramile.mpiapi.model.*;
import au.com.xtramile.mpiapi.repository.*;
import au.com.xtramile.mpiapi.service.PatientService;
import au.com.xtramile.mpiapi.util.MPIUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepo;
    private final PatientSourceRecordRepository patientSourceRecordRepo;
    private final PatientIdentifierRepository patientIdentifierRepo;
    private final PatientLinkRepository patientLinkRepo;
    private final PatientViewRepository patientViewRepo;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public Optional<Patient> findExactMatchByStrongIdentifier(PatientRequest request) {
        for (PatientIdentifierRequest identifier : request.identifiers()) {
            if (isStrongIdentifier(identifier.type())) {
                // Search in database
                Optional<PatientIdentifier> found = patientIdentifierRepo
                        .findByIdentifierTypeAndIdentifierValue(
                                identifier.type(),
                                identifier.value());

                if (found.isPresent()) {
                    return Optional.of(found.get().getPatient());
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean isStrongIdentifier(String type) {
        return "NATIONAL-ID".equals(type) ||
                "MRN".equals(type) ||
                "PASSPORT".equals(type);
    }

    @Override
    public List<Patient> findCandidatesByDemographics(PatientRequest request) {
        LocalDate dob = LocalDate.parse(request.dob(), formatter);

        // Search for patients with similar name and DOB (±2 years tolerance)
        return patientRepo.findCandidates(
                request.firstName(),
                request.lastName(),
                dob.minusYears(2),
                dob.plusYears(2)
        );
    }

    @Override
    public Patient findById(UUID id) {
        return patientRepo.findById(id).orElse(null);
    }

    @Override
    public void updatePatientIfNeeded(Patient existing, PatientRequest incoming) {
        boolean updated = false;

        // Update if incoming data is more complete
        if (MPIUtil.isBlank(existing.getAddress()) && MPIUtil.isNotBlank(incoming.address())) {
            existing.setAddress(incoming.address());
            updated = true;
        }

        if (MPIUtil.isBlank(existing.getSuburb()) && MPIUtil.isNotBlank(incoming.suburb())) {
            existing.setSuburb(incoming.suburb());
            updated = true;
        }

        if (MPIUtil.isBlank(existing.getState()) && MPIUtil.isNotBlank(incoming.state())) {
            existing.setState(incoming.state());
            updated = true;
        }

        if (MPIUtil.isBlank(existing.getPostalCode()) && MPIUtil.isNotBlank(incoming.postalCode())) {
            existing.setPostalCode(incoming.postalCode());
            updated = true;
        }

        if (updated) patientRepo.save(existing);
    }

    @Override
    public void addIdentifiers(Patient patient, List<PatientIdentifierRequest> identifiers) {
        for (PatientIdentifierRequest obj : identifiers) {
            // Check if identifier already exists
            Optional<PatientIdentifier> existing = patientIdentifierRepo.findByIdentifierTypeAndIdentifierValue(
                    obj.type(),
                    obj.value());

            if (existing.isEmpty()) {
                PatientIdentifier identifier = PatientIdentifier.builder()
                        .id(UUID.randomUUID())
                        .patient(patient)
                        .identifierType(obj.type())
                        .identifierValue(obj.value())
                        .issuingAuthority(obj.issuingAuthority())
                        .verified(false)
                        .build();

                patientIdentifierRepo.save(identifier);
            }
        }
    }

    @Override
    public void createSourceRecord(Patient patient, PatientRequest incoming, SourceSystem sourceSystem) {
        PatientSourceRecord record = PatientSourceRecord.builder()
                .id(UUID.randomUUID())
                .patient(patient)
                .sourceSystem(sourceSystem)
                .externalPatientId(incoming.externalPatientId())
                .rawData("")
                .receivedAt(LocalDateTime.now())
                .build();

        patientSourceRecordRepo.save(record);
    }

    @Override
    public PatientLink createPatientLink(Patient source, Patient target, String status, int score, Map<String, Boolean> fieldMatches) {
        ObjectMapper mapper = new ObjectMapper();

        PatientLink link = PatientLink.builder()
                .id(UUID.randomUUID())
                .sourcePatient(source)
                .targetPatient(target)
                .linkStatus(status)
                .confidenceScore(score)
                .fieldMatches(mapper.writeValueAsString(fieldMatches))
                .createdBy("SYSTEM")
                .build();

        return patientLinkRepo.save(link);
    }

    @Override
    public Patient createNewPatient(PatientRequest request) {
        Patient patient = Patient.builder()
                .id(UUID.randomUUID())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .dob(LocalDate.parse(request.dob(), formatter))
                .gender(request.gender())
                .phoneNo(request.phoneNo())
                .address(request.address())
                .suburb(request.suburb())
                .state(request.state())
                .postalCode(request.postalCode())
                .country(request.country())
                .email(request.email())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        return patientRepo.save(patient);
    }

    @Override
    public PaginatedResponse<PatientResponse> getListPagination(PaginationRequest pageRequest, String keyword, String status) {
        try {
            Sort.Direction direction = pageRequest.sortOrder().equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;

            Page<PatientResponse> res = patientViewRepo.getListPageable(
                            keyword.isEmpty() ? null : keyword.toLowerCase(),
                            status.isEmpty() ? null : status,
                            PageRequest.of(
                                    pageRequest.page(),
                                    pageRequest.size(),
                                    Sort.by(direction, pageRequest.sortBy())
                            ))
                    .map(pv -> {
                        List<PatientIdentifier> patientIdentifiers = patientIdentifierRepo.findByPatientId(pv.getId());
                        return transformView(pv, patientIdentifiers);
                    });

            return PaginatedResponse.of(
                    res.getContent(),
                    pageRequest.page(),
                    pageRequest.size(),
                    res.getTotalElements());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public PatientResponse transformView(PatientView patientView, List<PatientIdentifier> patientIdentifiers) {
        PatientResponse res = null;

        try {
            res = new PatientResponse();

            List<PatientIdentifierResponse> identifiers = new ArrayList<>();
            for (PatientIdentifier obj : patientIdentifiers) {
                identifiers.add(PatientIdentifierResponse.builder()
                        .id(obj.getId())
                        .type(obj.getIdentifierType())
                        .value(obj.getIdentifierValue())
                        .issuingAuthority(obj.getIssuingAuthority())
                        .verified(obj.isVerified())
                        .build());
            }

            BeanUtils.copyProperties(patientView, res);
            res.setIdentifiers(identifiers);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}
