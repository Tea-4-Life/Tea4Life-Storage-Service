package tea4life.storage_service.consumer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tea4life.storage_service.service.StorageService;

/**
 * Admin 2/12/2026
 *
 **/
@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StorageConsumer {

    StorageService storageService;

    @KafkaListener(topics = "storage-delete-file-topic", groupId = "tea4life-storage-group")
    public void listenDelete(String objectKey) {
        log.info("Received delete request from Kafka: {}", objectKey);
        storageService.deleteFile(objectKey);
    }
    
}
