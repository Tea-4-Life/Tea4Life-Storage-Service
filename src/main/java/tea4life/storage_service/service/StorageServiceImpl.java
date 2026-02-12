package tea4life.storage_service.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CopyRequest;
import tea4life.storage_service.dto.request.FileMoveRequest;
import tea4life.storage_service.dto.request.PresignedUrlRequest;
import tea4life.storage_service.dto.response.PresignedUrlResponse;

import java.time.Duration;
import java.util.UUID;

/**
 * Admin 2/4/2026
 *
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StorageServiceImpl implements StorageService {

    S3Presigner s3Presigner;
    S3TransferManager s3TransferManager;
    S3AsyncClient s3AsyncClient;

    @Value("${aws.s3.bucket-name}")
    @NonFinal
    String bucketName;

    /**
     * Giai đoạn 1: Cấp "vé" cho Frontend upload ảnh trực tiếp lên S3.
     * File sẽ được lưu tạm vào thư mục 'temp/' để chờ xác nhận.
     */
    @Override
    public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request) {
        String objectKey = "temp/" + UUID.randomUUID() + "-" + request.fileName();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(request.contentType())
                .build();

        // Ký tên vào URL với thời hạn 10 phút
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        String uploadUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
        log.info("Generated presigned URL for object: {}", objectKey);

        return new PresignedUrlResponse(uploadUrl, objectKey);
    }

    /**
     * Giai đoạn 2: Chốt hạ dữ liệu. Di chuyển file từ 'temp/' sang thư mục chính thức.
     * Sử dụng S3TransferManager (CRT) để thực hiện copy đa luồng tốc độ cao.
     */
    @Override
    public String confirmAndMoveFile(FileMoveRequest request) {
        // Tạo đường dẫn mới (ví dụ: private/avatar_abc.jpg)
        String destinationKey = request.destinationPath() + "/" + request.tempKey().replace("temp/", "");

        log.debug("Moving file from {} to {}", request.tempKey(), destinationKey);

        // Thực hiện lệnh Copy nội bộ trên S3 (không tốn băng thông server)
        CopyRequest copyRequest = CopyRequest
                .builder()
                .copyObjectRequest(CopyObjectRequest.builder()
                        .sourceBucket(bucketName)
                        .sourceKey(request.tempKey())
                        .destinationBucket(bucketName)
                        .destinationKey(destinationKey)
                        .build())
                .build();

        // .join() sẽ đợi cho đến khi copy xong. Nhờ Virtual Threads nên sẽ không block hệ thống
        s3TransferManager.copy(copyRequest).completionFuture().join();

        // Xóa file cũ ở folder temp sau khi đã copy thành công
        s3AsyncClient.deleteObject(DeleteObjectRequest
                .builder()
                .bucket(bucketName)
                .key(request.tempKey())
                .build());

        log.info("File confirmed and moved to: {}", destinationKey);
        return destinationKey;
    }

    @Override
    public void deleteFile(String objectKey) {
        log.info("Deleting object from S3: {}", objectKey);

        s3AsyncClient.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build())
                .thenRun(() -> log.info("Successfully deleted: {}", objectKey))
                .exceptionally(ex -> {
                    log.error("Failed to delete object {}: {}", objectKey, ex.getMessage());
                    return null;
                });
    }

}
