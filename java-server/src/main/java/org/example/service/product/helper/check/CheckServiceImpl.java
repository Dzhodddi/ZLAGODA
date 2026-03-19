package org.example.service.product.helper.check;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.dto.helper.CheckResponseDto;
import org.example.repository.product.helper.CheckRepository;
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
