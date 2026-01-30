package org.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class RestPage<T> extends PageImpl<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPage(@JsonProperty("content") List<T> content,
                    @JsonProperty("pageNumber") int pageNumber,
                    @JsonProperty("pageSize") int pageSize,
                    @JsonProperty("totalElements") Long totalElements) {
        super(content, PageRequest.of(
                        pageNumber,
                        pageSize > 0 ? pageSize : 1),
                totalElements != null ? totalElements : 0);
    }


    public RestPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public RestPage(List<T> content) {
        super(content);
    }

    public RestPage() {
        super(new ArrayList<>());
    }
}
