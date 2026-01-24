package au.com.xtramile.mpiapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {

    private List<T> entries;
    private PaginationInfoResponse paginationInfo;

    public static <T> PaginatedResponse<T> of(List<T> entries, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PaginatedResponse.<T>builder()
                .entries(entries)
                .paginationInfo(PaginationInfoResponse.builder()
                        .page(page)
                        .size(size)
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .hasNext(page < totalPages - 1)
                        .hasPrevious(page > 0)
                        .build())
                .build();
    }

}
