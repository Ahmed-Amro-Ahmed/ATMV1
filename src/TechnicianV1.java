import java.util.List;

public class TechnicianV1 {

    // Dependency Inversion: Depends on interface, not specific class
    protected List<IStatusView> components;

    public TechnicianV1(List<IStatusView> components) {
        this.components = components;
    }

    public void checkInventory() {
        System.out.println("--- TECH V1 DIAGNOSTIC REPORT ---");
        for (IStatusView comp : components) {
            System.out.println("Status: " + comp.getStatus());
            System.out.println("Firmware: " + comp.getFirmwareVersion());
            System.out.println("---------------------------------");
        }
    }
}