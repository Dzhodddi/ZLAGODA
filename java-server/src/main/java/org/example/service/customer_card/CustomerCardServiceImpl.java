package org.example.service.customer_card;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.dto.helper.CustomerCardResponseDto;
import org.example.repository.customer_card.CustomerCardRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomerCardServiceImpl implements CustomerCardService {

    private final CustomerCardRepository customerCardRepository;

    @Override
    public List<CustomerCardResponseDto> getAll() {
        return customerCardRepository.findAll();
    }
}
