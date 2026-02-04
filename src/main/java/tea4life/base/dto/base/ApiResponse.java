package tea4life.base.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Admin 8/3/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private String errorMessage;
    private Integer errorCode;
    private T data;

    // ==================================
    // Trả về API Response chứa lỗi
    // data sẽ null
    // ==================================
    public ApiResponse(String errorMessage, Integer errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.data = null;
    }

    // ==================================
    // Trả về API Response bình thường
    // errorMessage và errorCode sẽ null
    // ==================================
    public ApiResponse(T data) {
        this.data = data;
        this.errorMessage = null;
        this.errorCode = null;
    }
}
