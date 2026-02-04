package tea4life.base.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

/**
 * Admin 2/4/2026
 * Tối ưu hóa hiệu năng bằng động cơ CRT (Common Runtime) cho các thao tác file lớn
 **/
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class S3Config {

    @Value("${aws.s3.access-key}")
    String accessKey;

    @Value("${aws.s3.secret-key}")
    String secretKey;

    @Value("${aws.s3.region}")
    String region;

    /**
     * Khởi tạo thông tin xác thực (Credentials) từ Access Key và Secret Key
     */
    private StaticCredentialsProvider getCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    /**
     * Khởi tạo S3 Async Client với động cơ CRT (C++ Native)
     */
    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient
                .crtBuilder()
                .region(Region.of(region))
                .credentialsProvider(getCredentialsProvider())
                .build();
    }

    /**
     * Quản lý việc truyền tải dữ liệu (Upload/Download/Copy)
     * Tự động chia nhỏ file (Multi-part) và thực hiện song song để đạt tốc độ tối đa
     * Được dùng chính cho luồng "Move" file từ thư mục temp sang folder khác
     */
    @Bean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient) {
        return S3TransferManager
                .builder()
                .s3Client(s3AsyncClient)
                .build();
    }

    /**
     * Thành phần ký số (Signing) để tạo các URL có thời hạn (Presigned URL)
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner
                .builder()
                .region(Region.of(region))
                .credentialsProvider(getCredentialsProvider())
                .build();
    }


}
