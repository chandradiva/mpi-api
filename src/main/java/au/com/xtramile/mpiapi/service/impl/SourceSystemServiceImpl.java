package au.com.xtramile.mpiapi.service.impl;

import au.com.xtramile.mpiapi.model.SourceSystem;
import au.com.xtramile.mpiapi.repository.SourceSystemRepository;
import au.com.xtramile.mpiapi.service.SourceSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceSystemServiceImpl implements SourceSystemService {

    private final SourceSystemRepository sourceSystemRepo;

    @Override
    public List<SourceSystem> findAll() {
        return sourceSystemRepo.findAll();
    }

}
