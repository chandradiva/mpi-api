package au.com.xtramile.mpiapi.controller;

import au.com.xtramile.mpiapi.dto.request.PaginationRequest;
import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.dto.response.ApiResponseDto;
import au.com.xtramile.mpiapi.dto.response.PaginatedResponse;
import au.com.xtramile.mpiapi.dto.response.PatientResponse;
import au.com.xtramile.mpiapi.service.PatientService;
import au.com.xtramile.mpiapi.util.CommonCons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping
    public ResponseEntity getList(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "") String status
    ) {
        try {
            PaginationRequest pageRequest = new PaginationRequest(page, size, sortBy, sortOrder);
            PaginatedResponse<PatientResponse> res = patientService.getListPagination(pageRequest, keyword, status);

            return ResponseEntity.ok(
                    ApiResponseDto.builder()
                            .code(CommonCons.RES_SUCCESS_CODE)
                            .message(CommonCons.RES_SUCCESS_MSG)
                            .data(res)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponseDto.error("500", "Error retrieving Master Type Data"));
        }
    }

    @PostMapping
    public ResponseEntity createPatient(@RequestBody PatientRequest request) {
        PatientResponse res = patientService.saveData(request);

        return ResponseEntity.ok(
                ApiResponseDto.builder()
                        .code(CommonCons.RES_SUCCESS_CODE)
                        .message(CommonCons.RES_SUCCESS_MSG)
                        .data(res)
                        .build()
        );
    }

}
