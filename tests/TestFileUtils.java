import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestFileUtils {
    private TestFileUtils() {
    }

    public static String readFileOrNull(String path) {
        try {
            Path filePath = Path.of(path);
            if (!Files.exists(filePath)) {
                return null;
            }
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    public static void writeFile(String path, String content) {
        try {
            Files.writeString(Path.of(path), content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write file: " + path, e);
        }
    }

    public static void restoreOrDelete(String path, String originalContent) {
        try {
            Path filePath = Path.of(path);
            if (originalContent == null) {
                Files.deleteIfExists(filePath);
            } else {
                writeFile(path, originalContent);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore file: " + path, e);
        }
    }
}
