package au.com.xtramile.mpiapi.controller;

import au.com.xtramile.mpiapi.dto.response.ApiResponseDto;
import au.com.xtramile.mpiapi.model.SourceSystem;
import au.com.xtramile.mpiapi.service.SourceSystemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SourceSystemControllerTest {

    @Mock
    private SourceSystemService sourceSystemService;

    @InjectMocks
    private SourceSystemController controller;

    @Test
    void findAll_returnsApiResponseWithSourceSystems() {
        UUID id = UUID.randomUUID();
        SourceSystem s = SourceSystem.builder()
                .id(id)
                .systemName("RS Hasan Sadikin")
                .systemCode("RSHS")
                .active(true)
                .build();

        when(sourceSystemService.findAll()).thenReturn(List.of(s));

        ResponseEntity<?> resp = controller.findAll();

        assertNotNull(resp);
        assertTrue(resp.getStatusCode().is2xxSuccessful());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody() instanceof ApiResponseDto);

        ApiResponseDto<?> body = (ApiResponseDto<?>) resp.getBody();
        assertEquals("200", body.getCode());
        assertEquals("Success", body.getMessage());
        assertTrue(body.getData() instanceof List);
        List<?> data = (List<?>) body.getData();
        assertEquals(1, data.size());
        assertEquals(s, data.get(0));
    }
}
