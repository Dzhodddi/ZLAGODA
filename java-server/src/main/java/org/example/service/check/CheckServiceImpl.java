package org.example.service.check;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.helper.CheckResponseDto;
import org.example.repository.check.CheckRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckServiceImpl implements CheckService {

    private final CheckRepository checkRepository;

    @Override
    public List<CheckResponseDto> getAll() {
        return checkRepository.findAll();
    }
}
