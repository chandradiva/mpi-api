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
import au.com.xtramile.mpiapi.util.CommonCons;
import au.com.xtramile.mpiapi.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepo;
    private final HospitalRepository hospitalRepo;
    private final PatientRecordRepository patientRecordRepo;
    private final PatientDemographicRepository patientDemographicRepo;
    private final PatientIdentifierRepository patientIdentifierRepo;
    private final PatientViewRepository patientViewRepo;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    @Override
    public PatientResponse saveData(PatientRequest request) {
        try {
            Hospital hospital = null;
            if (request.hospitalId() != null) {
                Optional<Hospital> optHospital = hospitalRepo.findById(request.hospitalId());
                if (optHospital.isPresent()) {
                    hospital = optHospital.get();
                }
            }

            Patient patient = Patient.builder()
                    .id(UUID.randomUUID())
                    .mpiScore(new BigDecimal("0.0"))
                    .createdAt(LocalDateTime.now())
                    .status(CommonCons.STATUS_ACTIVE)
                    .build();

            patientRepo.save(patient);

            PatientRecord patientRecord = PatientRecord.builder()
                    .id(UUID.randomUUID())
                    .hospital(hospital)
                    .patient(patient)
                    .matchStatus(CommonCons.MATCH_STATUS_PENDING)
                    .recordNumber(request.recordNumber())
                    .createdAt(LocalDateTime.now())
                    .build();

            patientRecordRepo.save(patientRecord);

            PatientDemographic patientDemographic = PatientDemographic.builder()
                    .id(UUID.randomUUID())
                    .recordId(patientRecord.getId())
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .firstNameNorm(CommonUtil.normalizeName(request.firstName()))
                    .lastNameNorm(CommonUtil.normalizeName(request.lastName()))
                    .dob(LocalDate.parse(request.dob(), formatter))
                    .gender(request.gender())
                    .phone(request.phone())
                    .phoneNorm(CommonUtil.normalizePhone(request.phone()))
                    .email(request.email())
                    .emailNorm(CommonUtil.normalizeEmail(request.email()))
                    .address(request.address())
                    .suburb(request.suburb())
                    .state(request.state())
                    .postcode(request.postcode())
                    .build();

            patientDemographicRepo.save(patientDemographic);

            List<PatientIdentifier> listIdentifier = null;
            if (request.identifiers() != null && !request.identifiers().isEmpty()) {
                listIdentifier = new ArrayList<>();

                for (PatientIdentifierRequest identifier : request.identifiers()) {
                    listIdentifier.add(PatientIdentifier.builder()
                            .id(UUID.randomUUID())
                            .recordId(patientRecord.getId())
                            .type(identifier.type())
                            .value(identifier.value())
                            .build());
                }

                if (!listIdentifier.isEmpty()) patientIdentifierRepo.saveAll(listIdentifier);
            }

            return transform(patient, patientDemographic, patientRecord, listIdentifier);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public PatientResponse updateData(UUID id, PatientRequest request) {
        return null;
    }

    @Override
    public PaginatedResponse<PatientResponse> getListPagination(PaginationRequest pageRequest, String keyword, String status) {
        try {
            Sort.Direction direction = pageRequest.sortOrder().equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;

            Page<PatientResponse> res = patientViewRepo.getListPageable(
                            keyword.isEmpty() ? null : keyword.toUpperCase(),
                            status.isEmpty() ? null : status,
                            PageRequest.of(
                                    pageRequest.page(),
                                    pageRequest.size(),
                                    Sort.by(direction, pageRequest.sortBy())
                            ))
                    .map(pv -> {
                        List<PatientIdentifier> patientIdentifiers = patientIdentifierRepo.findByRecordId(pv.getRecordId());
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

    @Override
    public PatientResponse transform(Patient patient, PatientDemographic patientDemographic, PatientRecord patientRecord, List<PatientIdentifier> patientIdentifiers) {
        PatientResponse res = null;

        try {
            List<PatientIdentifierResponse> identifiers = new ArrayList<>();
            for (PatientIdentifier obj : patientIdentifiers) {
                identifiers.add(PatientIdentifierResponse.builder()
                        .id(obj.getId())
                        .type(obj.getType())
                        .value(obj.getValue())
                        .build());
            }

            res = PatientResponse.builder()
                    .id(patient.getId())
                    .mpiScore(patient.getMpiScore())
                    .createdAt(patient.getCreatedAt())
                    .updatedAt(patient.getUpdatedAt())
                    .status(patient.getStatus())
                    .recordId(patientRecord.getId())
                    .hospitalId(patientRecord.getHospital() == null ? "" : patientRecord.getHospital().getId())
                    .hospitalName(patientRecord.getHospital() == null ? "" : patientRecord.getHospital().getName())
                    .recordNumber(patientRecord.getRecordNumber())
                    .matchStatus(patientRecord.getMatchStatus())
                    .demographicId(patientDemographic.getId())
                    .firstName(patientDemographic.getFirstName())
                    .lastName(patientDemographic.getLastName())
                    .firstNameNorm(patientDemographic.getFirstNameNorm())
                    .lastNameNorm(patientDemographic.getLastNameNorm())
                    .dob(patientDemographic.getDob())
                    .gender(patientDemographic.getGender())
                    .phone(patientDemographic.getPhone())
                    .phoneNorm(patientDemographic.getPhoneNorm())
                    .email(patientDemographic.getEmail())
                    .emailNorm(patientDemographic.getEmailNorm())
                    .address(patientDemographic.getAddress())
                    .suburb(patientDemographic.getSuburb())
                    .state(patientDemographic.getState())
                    .postcode(patientDemographic.getPostcode())
                    .identifiers(identifiers)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public PatientResponse transformView(PatientView patientView, List<PatientIdentifier> patientIdentifiers) {
        PatientResponse res = null;

        try {
            res = new PatientResponse();

            List<PatientIdentifierResponse> identifiers = new ArrayList<>();
            for (PatientIdentifier obj : patientIdentifiers) {
                identifiers.add(PatientIdentifierResponse.builder()
                        .id(obj.getId())
                        .type(obj.getType())
                        .value(obj.getValue())
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
