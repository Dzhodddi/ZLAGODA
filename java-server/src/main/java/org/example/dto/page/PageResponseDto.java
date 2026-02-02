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
    private int pageSize;
    private long totalElements;
    private boolean hasNext;

    public static <T> PageResponseDto<T> of(List<T> content,
                                            int pageSize,
                                            long totalElements,
                                            boolean hasNext) {
        return new PageResponseDto<>(content,
                pageSize,
                totalElements,
                hasNext);
    }
}
