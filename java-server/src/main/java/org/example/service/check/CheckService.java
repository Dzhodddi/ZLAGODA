package org.example.service.check;

import java.util.List;
import org.example.dto.helper.CheckResponseDto;

public interface CheckService {
    List<CheckResponseDto> getAll();
}
