package au.com.xtramile.mpiapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponseDto<T> success(String code, String message, T data) {
        return ApiResponseDto.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDto<T> error(String code, String message) {
        return ApiResponseDto.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
