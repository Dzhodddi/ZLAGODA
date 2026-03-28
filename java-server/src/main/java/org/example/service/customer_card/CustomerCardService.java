package org.example.service.customer_card;

import java.util.List;
import org.example.dto.helper.CustomerCardResponseDto;

public interface CustomerCardService {
    List<CustomerCardResponseDto> getAll();
}
