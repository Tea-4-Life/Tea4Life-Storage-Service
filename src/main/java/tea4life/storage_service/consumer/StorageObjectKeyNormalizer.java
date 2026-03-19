package tea4life.storage_service.consumer;

final class StorageObjectKeyNormalizer {

    private StorageObjectKeyNormalizer() {
    }

    static String normalize(String rawObjectKey) {
        if (rawObjectKey == null) {
            return null;
        }

        String normalizedKey = rawObjectKey.trim();
        if (normalizedKey.length() >= 2
                && normalizedKey.startsWith("\"")
                && normalizedKey.endsWith("\"")) {
            normalizedKey = normalizedKey.substring(1, normalizedKey.length() - 1);
        }
        return normalizedKey;
    }
}
