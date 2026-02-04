import java.io.*;
import java.util.*;

public class FileServiceV1 {
    public static void saveToFile(String filename, String jsonData) {
        try (FileWriter file = new FileWriter("V1/" + filename)) {
            file.write(jsonData);
            file.flush();
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Could not write to disk: " + e.getMessage());
        }
    }

    // Appends a transaction as a valid JSON array entry
    public static void appendTransactionToJsonArray(String filename, String jsonObject) {
        try {
            File file = new File("V1/" + filename);
            List<String> lines = new ArrayList<>();
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }
            if (lines.isEmpty()) {
                // New file, create array
                lines.add("[");
                lines.add(jsonObject);
                lines.add("]");
            } else {
                // Insert before last ]
                int lastIdx = lines.size() - 1;
                if (lines.get(lastIdx).trim().equals("]")) {
                    if (lastIdx > 1) {
                        lines.set(lastIdx, ",");
                        lines.add(jsonObject);
                        lines.add("]");
                    } else {
                        // Only [ and ], add first object
                        lines.add(lastIdx, jsonObject);
                    }
                } else {
                    // Corrupt file, reset
                    lines.clear();
                    lines.add("[");
                    lines.add(jsonObject);
                    lines.add("]");
                }
            }
            try (FileWriter writer = new FileWriter(file, false)) {
                for (String l : lines) {
                    writer.write(l + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Could not append transaction: " + e.getMessage());
        }
    }
}
