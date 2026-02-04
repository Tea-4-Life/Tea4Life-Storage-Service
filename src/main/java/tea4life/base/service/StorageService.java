package tea4life.base.service;

import tea4life.base.dto.request.FileMoveRequest;
import tea4life.base.dto.request.PresignedUrlRequest;
import tea4life.base.dto.response.PresignedUrlResponse;

/**
 * Admin 2/4/2026
 *
 **/
public interface StorageService {
    PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request);

    String confirmAndMoveFile(FileMoveRequest request);
}
