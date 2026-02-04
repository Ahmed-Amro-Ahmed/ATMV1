import org.json.JSONObject;
import java.io.File;
import java.nio.file.Files;

public class PrinterV1 implements IPrinter, IPrinterSupplies, IMaintainable, IStatusView {
    private int paperLevel = 100;
    private int inkLevel = 100;
    private String firmwareVersion = "1.0";

    public PrinterV1() {
        // Try to load state from inventory.json
        try {
            File file = new File("V1/inventory.json");
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                JSONObject obj = new JSONObject(content);
                if (obj.has("paperLevel")) paperLevel = obj.getInt("paperLevel");
                if (obj.has("inkLevel")) inkLevel = obj.getInt("inkLevel");
                if (obj.has("printerFirmware")) firmwareVersion = obj.getString("printerFirmware");
            }
        } catch (Exception e) {
            // Ignore, use defaults
        }
    }

    public void printReceipt() {
        // Check if printer has supplies
        if (paperLevel <= 0 && inkLevel <= 0) {
            System.out.println("Out of paper and ink. Transaction completed without receipt.");
            return;
        } else if (paperLevel <= 0) {
            System.out.println("Out of paper. Transaction completed without receipt.");
            return;
        } else if (inkLevel <= 0) {
            System.out.println("Out of ink. Transaction completed without receipt.");
            return;
        }
        
        // Each receipt uses 1% paper and 1% ink
        paperLevel--;
        inkLevel--;
        System.out.println("[PRINTER]: Receipt printed successfully. Paper: " + paperLevel + "%, Ink: " + inkLevel + "%");
    }

    @Override
    public void printReceipt(String content) {
        printReceipt();
    }

    @Override
    public void refillInk() {
        inkLevel = 100;
    }

    @Override
    public void refillPaper() {
        paperLevel = 100;
    }

    @Override
    public int getPaperLevel() {
        return paperLevel;
    }

    @Override
    public int getInkLevel() {
        return inkLevel;
    }

    @Override
    public String getStatus() {
        return "Printer Paper Level: " + paperLevel + "% | Ink Level: " + inkLevel + "%";
    }

    @Override
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    @Override
    public void updateFirmware(String newVersion) {
        firmwareVersion = newVersion;
    }
}
