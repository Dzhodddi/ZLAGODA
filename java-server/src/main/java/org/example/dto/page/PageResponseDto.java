package org.example.dto.page;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public static <T> PageResponseDto<T> of(List<T> content,
                                            int pageNumber,
                                            int pageSize,
                                            long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return new PageResponseDto<>(content,
                pageNumber,
                pageSize,
                totalElements,
                totalPages);
    }
}
