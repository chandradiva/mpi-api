package au.com.xtramile.mpiapi.service.impl;

import au.com.xtramile.mpiapi.dto.MatchResultDto;
import au.com.xtramile.mpiapi.dto.request.PatientIdentifierRequest;
import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.dto.response.ProcessingResultResponse;
import au.com.xtramile.mpiapi.model.Patient;
import au.com.xtramile.mpiapi.model.SourceSystem;
import au.com.xtramile.mpiapi.repository.SourceSystemRepository;
import au.com.xtramile.mpiapi.service.PatientMatchingService;
import au.com.xtramile.mpiapi.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MPIServiceImplTest {

    private PatientService patientService;
    private PatientMatchingService patientMatchingService;
    private SourceSystemRepository sourceSystemRepo;

    private MPIServiceImpl svc;

    @BeforeEach
    void setUp() {
        patientService = mock(PatientService.class);
        patientMatchingService = mock(PatientMatchingService.class);
        sourceSystemRepo = mock(SourceSystemRepository.class);

        svc = new MPIServiceImpl(patientService, patientMatchingService, sourceSystemRepo);
    }

    @Test
    void processingIncomingPatient_exactStrongIdentifier_shouldAutoMatch() {
        UUID sysId = UUID.randomUUID();
        UUID existingId = UUID.randomUUID();

        PatientRequest req = new PatientRequest(
                null,
                "Fredilla",
                "Diva",
                "17/05/1991",
                "MALE",
                "08121010101",
                "diva@gmail.com",
                null, null, null, null, null,
                sysId,
                "ext-1",
                List.of(new PatientIdentifierRequest("NATIONAL-ID", "123", "NATIONAL", true))
        );

        Patient existing = Patient.builder()
                .id(existingId)
                .firstName("fredilla")
                .lastName("diva")
                .dob(LocalDate.parse("1991-05-17"))
                .build();

        when(sourceSystemRepo.findById(eq(sysId))).thenReturn(Optional.of(SourceSystem.builder().id(sysId).systemName("Test").build()));
        when(patientService.findExactMatchByStrongIdentifier(any())).thenReturn(Optional.of(existing));

        ProcessingResultResponse res = svc.processingIncomingPatient(req);

        assertNotNull(res);
        assertEquals("AUTO_MATCH", res.getStatus());
        assertEquals(existingId, res.getPatientId());
        assertEquals("STRONG_IDENTIFIER", res.getMatchedVia());

        verify(patientService).updatePatientIfNeeded(eq(existing), any());
        verify(patientService).addIdentifiers(eq(existing), anyList());
        verify(patientService).createSourceRecord(eq(existing), any(), any());
        verify(patientService).createPatientLink(eq(existing), eq(existing), eq("AUTO_MATCH"), anyInt(), any(Map.class));
    }

    @Test
    void processingIncomingPatient_noCandidates_shouldCreateNewPatient() {
        UUID sysId = UUID.randomUUID();

        PatientRequest req = new PatientRequest(
                null,
                "New",
                "Patient",
                "01/01/2000",
                "FEMALE",
                "08129998877",
                "new@example.com",
                null, null, null, null, null,
                sysId,
                "ext-2",
                List.of()
        );

        when(sourceSystemRepo.findById(eq(sysId))).thenReturn(Optional.of(SourceSystem.builder().id(sysId).systemName("Test").build()));
        when(patientService.findExactMatchByStrongIdentifier(any())).thenReturn(Optional.empty());
        when(patientService.findCandidatesByDemographics(any())).thenReturn(List.of());

        when(patientService.createNewPatient(any())).thenAnswer(invocation -> {
            PatientRequest p = invocation.getArgument(0);
            Patient created = Patient.builder().id(UUID.randomUUID()).firstName(p.firstName()).lastName(p.lastName()).dob(LocalDate.parse("2000-01-01")).build();
            return created;
        });

        ProcessingResultResponse res = svc.processingIncomingPatient(req);

        assertNotNull(res);
        assertEquals("NO_MATCH", res.getStatus());
        assertEquals(0, res.getConfidenceScore());

        verify(patientService).createNewPatient(any());
        verify(patientService).addIdentifiers(any(), anyList());
        verify(patientService).createSourceRecord(any(), any(), any());
    }

    @Test
    void processingIncomingPatient_demographicsAutoMatch_shouldAutoMatch() {
        UUID sysId = UUID.randomUUID();
        UUID matchedId = UUID.randomUUID();

        PatientRequest req = new PatientRequest(
                null,
                "Fredilla",
                "Diva",
                "17/05/1991",
                "MALE",
                "08121010101",
                "diva@gmail.com",
                null, null, null, null, null,
                sysId,
                "ext-3",
                List.of()
        );

        Patient candidate = Patient.builder().id(matchedId).firstName("fredilla").lastName("diva").dob(LocalDate.parse("1991-05-17")).build();

        when(sourceSystemRepo.findById(eq(sysId))).thenReturn(Optional.of(SourceSystem.builder().id(sysId).systemName("Test").build()));
        when(patientService.findExactMatchByStrongIdentifier(any())).thenReturn(Optional.empty());
        when(patientService.findCandidatesByDemographics(any())).thenReturn(List.of(candidate));

        MatchResultDto match = new MatchResultDto("AUTO_MATCH", 85, matchedId, Map.of("name", true));
        when(patientMatchingService.findMatch(any(), anyList())).thenReturn(match);
        when(patientService.findById(eq(matchedId))).thenReturn(candidate);

        ProcessingResultResponse res = svc.processingIncomingPatient(req);

        assertNotNull(res);
        assertEquals("AUTO_MATCH", res.getStatus());
        assertEquals(matchedId, res.getPatientId());

        verify(patientService).updatePatientIfNeeded(eq(candidate), any());
        verify(patientService).addIdentifiers(eq(candidate), anyList());
        verify(patientService).createSourceRecord(eq(candidate), any(), any());
        verify(patientService).createPatientLink(eq(candidate), eq(candidate), eq("AUTO_MATCH"), eq(85), anyMap());
    }
}
