package tea4life.storage_service.service;

import tea4life.storage_service.dto.request.FileMoveRequest;
import tea4life.storage_service.dto.request.PresignedUrlRequest;
import tea4life.storage_service.dto.response.PresignedUrlResponse;

/**
 * Admin 2/4/2026
 *
 **/
public interface StorageService {
    PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request);

    String confirmAndMoveFile(FileMoveRequest request);

    void deleteFile(String objectKey);
}
