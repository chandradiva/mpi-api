package au.com.xtramile.mpiapi.controller;

import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.dto.request.PaginationRequest;
import au.com.xtramile.mpiapi.dto.response.ApiResponseDto;
import au.com.xtramile.mpiapi.dto.response.PaginatedResponse;
import au.com.xtramile.mpiapi.dto.response.PatientResponse;
import au.com.xtramile.mpiapi.dto.response.ProcessingResultResponse;
import au.com.xtramile.mpiapi.service.MPIService;
import au.com.xtramile.mpiapi.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private MPIService mpiService;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController controller;

    @BeforeEach
    void setUp() {
    }

    @Test
    void processIncomingRequest_returnsSuccessResponseWithProcessingResult() {
        UUID patientId = UUID.randomUUID();
        ProcessingResultResponse proc = ProcessingResultResponse.builder()
                .status("NO_MATCH")
                .patientId(patientId)
                .confidenceScore(0)
                .message("New patient created")
                .build();

        when(mpiService.processingIncomingPatient(any(PatientRequest.class))).thenReturn(proc);

        PatientRequest req = new PatientRequest(
                null,
                "First",
                "Last",
                "01/01/1990",
                "MALE",
                "0812000000",
                "a@b.com",
                null, null, null, null, null,
                null,
                "ext",
                List.of()
        );

        ResponseEntity<?> resp = controller.processIncomingRequest(req);

        assertNotNull(resp);
        assertTrue(resp.getStatusCode().is2xxSuccessful());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody() instanceof ApiResponseDto);

        ApiResponseDto<?> body = (ApiResponseDto<?>) resp.getBody();
        assertEquals("200", body.getCode());
        assertEquals("Success", body.getMessage());
        assertEquals(proc, body.getData());
    }

    @Test
    void getList_returnsPaginatedPatientsWrappedInApiResponse() {
        UUID id = UUID.randomUUID();

        PatientResponse pr = PatientResponse.builder()
                .id(id)
                .firstName("Fred")
                .lastName("Diva")
                .dob(LocalDate.of(1991, 5, 17))
                .phoneNo("08121010101")
                .email("diva@gmail.com")
                .build();

        PaginatedResponse<PatientResponse> page = PaginatedResponse.of(List.of(pr), 0, 10, 1);

        when(patientService.getListPagination(any(PaginationRequest.class), anyString(), anyString())).thenReturn(page);

        ResponseEntity<?> resp = controller.getList(0, 10, "id", "DESC", "", "");

        assertNotNull(resp);
        assertTrue(resp.getStatusCode().is2xxSuccessful());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody() instanceof ApiResponseDto);

        ApiResponseDto<?> body = (ApiResponseDto<?>) resp.getBody();
        assertEquals("200", body.getCode());
        assertEquals("Success", body.getMessage());
        assertEquals(page, body.getData());
    }
}
