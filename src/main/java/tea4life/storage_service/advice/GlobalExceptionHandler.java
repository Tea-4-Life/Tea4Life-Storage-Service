package tea4life.storage_service.advice;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.s3.model.S3Exception;
import tea4life.storage_service.dto.base.ApiResponse;

/**
 * Admin 2/4/2026
 *
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bắt các lỗi đặc thù từ AWS S3
     */
    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<@NonNull ApiResponse<Void>> handleS3Exception(S3Exception e) {
        log.error("S3 Error: code={}, message={}", e.awsErrorDetails().errorCode(), e.getMessage());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .errorCode(e.statusCode())
                .errorMessage("Lỗi thao tác lưu trữ Cloud: " + e.awsErrorDetails().errorMessage())
                .build();

        return ResponseEntity.status(e.statusCode()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull ApiResponse<Void>> handleGeneralException(Exception e) {
        log.error("Unexpected Error: ", e);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .errorCode(500)
                .errorMessage("Hệ thống gặp sự cố ngoài ý muốn. Vui lòng thử lại sau.")
                .build();

        return ResponseEntity.internalServerError().body(response);
    }

}
