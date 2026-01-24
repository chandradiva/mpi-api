package au.com.xtramile.mpiapi.dto.request;

public record PaginationRequest(
        int page,
        int size,
        String sortBy,
        String sortOrder
) {

    public static PaginationRequest defaults() {
        return new PaginationRequest(0, 10, "id", "ASC");
    }

    public PaginationRequest(String sortBy, String sortOrder) {
        this(0, 10, sortBy, sortOrder);
    }

}
