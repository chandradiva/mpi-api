package au.com.xtramile.mpiapi.service.impl;

import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.dto.request.PaginationRequest;
import au.com.xtramile.mpiapi.dto.response.PaginatedResponse;
import au.com.xtramile.mpiapi.dto.response.PatientIdentifierResponse;
import au.com.xtramile.mpiapi.dto.response.PatientResponse;
import au.com.xtramile.mpiapi.model.Patient;
import au.com.xtramile.mpiapi.model.PatientIdentifier;
import au.com.xtramile.mpiapi.model.PatientView;
import au.com.xtramile.mpiapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PatientServiceImplTest {

    private PatientRepository patientRepo;
    private PatientSourceRecordRepository patientSourceRecordRepo;
    private PatientIdentifierRepository patientIdentifierRepo;
    private PatientLinkRepository patientLinkRepo;
    private PatientViewRepository patientViewRepo;

    private PatientServiceImpl svc;

    @BeforeEach
    void setUp() {
        patientRepo = mock(PatientRepository.class);
        patientSourceRecordRepo = mock(PatientSourceRecordRepository.class);
        patientIdentifierRepo = mock(PatientIdentifierRepository.class);
        patientLinkRepo = mock(PatientLinkRepository.class);
        patientViewRepo = mock(PatientViewRepository.class);

        svc = new PatientServiceImpl(patientRepo, patientSourceRecordRepo, patientIdentifierRepo, patientLinkRepo, patientViewRepo);
    }

    @Test
    void findCandidatesByDemographics_shouldCallRepoWithDobRange_andReturnResults() {
        PatientRequest request = new PatientRequest(
                UUID.randomUUID(),
                "Fredilla",
                "Diva",
                "17/05/1991",
                "MALE",
                "08121010101",
                "diva@gmail.com",
                null, null, null, null, null,
                null, null, null
        );

        Patient p = Patient.builder()
                .id(UUID.randomUUID())
                .firstName("Fredilla")
                .lastName("Diva")
                .dob(LocalDate.of(1991,5,17))
                .build();

        when(patientRepo.findCandidates(anyString(), anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(p));

        List<Patient> result = svc.findCandidatesByDemographics(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(p, result.get(0));

        ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endCaptor = ArgumentCaptor.forClass(LocalDate.class);

        verify(patientRepo).findCandidates(eq("Fredilla"), eq("Diva"), startCaptor.capture(), endCaptor.capture());

        LocalDate start = startCaptor.getValue();
        LocalDate end = endCaptor.getValue();

        assertEquals(LocalDate.of(1989,5,17), start);
        assertEquals(LocalDate.of(1993,5,17), end);
    }

    @Test
    void getListPagination_shouldTransformViewAndIncludeIdentifiers() {
        UUID id = UUID.randomUUID();

        PatientView pv = PatientView.builder()
                .id(id)
                .firstName("Fred")
                .lastName("Diva")
                .dob(LocalDate.of(1991,5,17))
                .phoneNo("08121010101")
                .email("diva@gmail.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .linkStatus("NO_MATCH")
                .confidenceScore(0)
                .externalPatientId("12345")
                .systemCode("RSHS")
                .systemName("RS Hasan Sadikin")
                .build();

        PatientIdentifier identifier = PatientIdentifier.builder()
                .id(UUID.randomUUID())
                .patientId(id)
                .identifierType("Email")
                .identifierValue("diva@gmail.com")
                .issuingAuthority("Self Reported")
                .verified(true)
                .build();

        when(patientViewRepo.getListPageable(any(), any(), any())).thenReturn(new PageImpl<>(List.of(pv)));
        when(patientIdentifierRepo.findByPatientId(eq(id))).thenReturn(List.of(identifier));

        PaginatedResponse<PatientResponse> res = svc.getListPagination(new PaginationRequest(0, 10, "id", "DESC"), "", "");

        assertNotNull(res);
        assertEquals(1, res.getEntries().size());

        PatientResponse pr = res.getEntries().get(0);
        assertEquals(pv.getId(), pr.getId());
        assertEquals("Fred", pr.getFirstName());
        assertNotNull(pr.getIdentifiers());
        assertEquals(1, pr.getIdentifiers().size());

        PatientIdentifierResponse pir = pr.getIdentifiers().get(0);
        assertEquals(identifier.getId(), pir.getId());
        assertEquals(identifier.getIdentifierType(), pir.getType());
        assertEquals(identifier.getIdentifierValue(), pir.getValue());
    }
}
