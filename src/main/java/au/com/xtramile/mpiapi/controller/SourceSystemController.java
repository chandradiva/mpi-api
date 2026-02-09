package au.com.xtramile.mpiapi.controller;

import au.com.xtramile.mpiapi.dto.response.ApiResponseDto;
import au.com.xtramile.mpiapi.model.SourceSystem;
import au.com.xtramile.mpiapi.service.SourceSystemService;
import au.com.xtramile.mpiapi.util.CommonCons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/source-systems")
public class SourceSystemController {

    @Autowired
    private SourceSystemService sourceSystemService;

    @GetMapping
    public ResponseEntity findAll() {
        List<SourceSystem> res = sourceSystemService.findAll();

        return ResponseEntity.ok(
                ApiResponseDto.builder()
                        .code(CommonCons.RES_SUCCESS_CODE)
                        .message(CommonCons.RES_SUCCESS_MSG)
                        .data(res)
                        .build());
    }

}
