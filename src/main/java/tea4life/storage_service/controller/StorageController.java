package tea4life.storage_service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tea4life.storage_service.dto.base.ApiResponse;
import tea4life.storage_service.dto.request.FileMoveRequest;
import tea4life.storage_service.dto.request.PresignedUrlRequest;
import tea4life.storage_service.dto.response.PresignedUrlResponse;
import tea4life.storage_service.service.StorageService;

/**
 * Admin 2/4/2026
 *
 **/
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StorageController {

    StorageService storageService;

    /**
     * Endpoint dành cho Frontend: Lấy link để tự upload ảnh.
     */
    @PostMapping("/storage/presigned-url")
    public ApiResponse<PresignedUrlResponse> getPresignedUrl(@RequestBody PresignedUrlRequest request) {
        return new ApiResponse<>(storageService.generatePresignedUrl(request));
    }

    /**
     * Endpoint nội bộ (gọi qua Feign): Xác nhận và di chuyển file.
     */
    @PostMapping("/storage/confirm")
    public ApiResponse<String> confirmFile(@RequestBody FileMoveRequest request) {
        return new ApiResponse<>(storageService.confirmAndMoveFile(request));
    }

}
