package org.example.controller.product;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.helper.CheckResponseDto;
import org.example.service.product.helper.check.CheckService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Check management",
        description = "Endpoints for managing checks")
@RequiredArgsConstructor
@RestController
@RequestMapping("/checks")
public class CheckController {

    private final CheckService checkService;

    @GetMapping()
    @Operation(
            summary = "Get all checks",
            description = "Get all checks"
    )
    @PreAuthorize("hasAnyAuthority('MANAGER')")
    public List<CheckResponseDto> getAll() {
        return checkService.getAll();
    }
}
