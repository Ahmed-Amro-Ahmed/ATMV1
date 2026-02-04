public class CashBinV1 implements ICashDispenser, IInventoryTracker, ICustomerAction, IStatusView {

    private int bill10Count;
    private int bill20Count;
    private int bill50Count;
    private int bill100Count;
    private int totalDeposits;
    private String firmwareVersion = "1.4";

    public CashBinV1(int startCash) {
        // Try to load state from inventory.json
        try {
            java.io.File file = new java.io.File("V1/inventory.json");
            if (file.exists()) {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                org.json.JSONObject obj = new org.json.JSONObject(content);
                this.bill10Count = obj.has("bill10Count") ? obj.getInt("bill10Count") : 0;
                this.bill20Count = obj.has("bill20Count") ? obj.getInt("bill20Count") : 0;
                this.bill50Count = obj.has("bill50Count") ? obj.getInt("bill50Count") : 0;
                this.bill100Count = obj.has("bill100Count") ? obj.getInt("bill100Count") : 100;
                this.totalDeposits = obj.has("depositsHeld") ? obj.getInt("depositsHeld") : 0;
            } else {
                // Default: 100 x $100 bills = $10,000
                this.bill100Count = startCash / 100;
                this.bill10Count = 0;
                this.bill20Count = 0;
                this.bill50Count = 0;
                this.totalDeposits = 0;
            }
        } catch (Exception e) {
            // If error loading, use defaults
            this.bill100Count = startCash / 100;
            this.bill10Count = 0;
            this.bill20Count = 0;
            this.bill50Count = 0;
            this.totalDeposits = 0;
        }
    }

    @Override
    public int getCashLevel() {
        return (bill10Count * 10) + (bill20Count * 20) + (bill50Count * 50) + (bill100Count * 100);
    }

    public int getTotalDeposits() {
        return totalDeposits;
    }
    
    public int getBill10Count() { return bill10Count; }
    public int getBill20Count() { return bill20Count; }
    public int getBill50Count() { return bill50Count; }
    public int getBill100Count() { return bill100Count; }

    @Override
    public boolean withdraw(int amount) {
        if (getCashLevel() < amount) {
            System.out.println("[ATM]: Error - Insufficient Funds");
            return false;
        }
        
        if (!canDispenseExactAmount(amount)) {
            System.out.println("[ATM]: Error - Cannot dispense exact amount with available bill denominations");
            return false;
        }
        
        dispenseBills(amount);
        System.out.println("[ATM]: Dispensing $" + amount);
        return true;
    }

    @Override
    public boolean dispense(int amount) {
        return withdraw(amount);
    }
    
    private boolean canDispenseExactAmount(int amount) {
        int remaining = amount;
        int temp100 = bill100Count;
        int temp50 = bill50Count;
        int temp20 = bill20Count;
        int temp10 = bill10Count;
        
        // Try to dispense using largest bills first
        int use100 = Math.min(remaining / 100, temp100);
        remaining -= use100 * 100;
        
        int use50 = Math.min(remaining / 50, temp50);
        remaining -= use50 * 50;
        
        int use20 = Math.min(remaining / 20, temp20);
        remaining -= use20 * 20;
        
        int use10 = Math.min(remaining / 10, temp10);
        remaining -= use10 * 10;
        
        return remaining == 0;
    }
    
    private void dispenseBills(int amount) {
        int remaining = amount;
        
        // Dispense largest bills first
        int use100 = Math.min(remaining / 100, bill100Count);
        bill100Count -= use100;
        remaining -= use100 * 100;
        
        int use50 = Math.min(remaining / 50, bill50Count);
        bill50Count -= use50;
        remaining -= use50 * 50;
        
        int use20 = Math.min(remaining / 20, bill20Count);
        bill20Count -= use20;
        remaining -= use20 * 20;
        
        int use10 = Math.min(remaining / 10, bill10Count);
        bill10Count -= use10;
        remaining -= use10 * 10;
    }

    @Override
    public void deposit(int amount) {
        totalDeposits += amount;
        // Assume deposits are in $100 bills for simplicity
        bill100Count += amount / 100;
        System.out.println("[ATM]: Deposit Accepted: $" + amount);
    }

    @Override
    public boolean hasCash(int amount) {
        return getCashLevel() >= amount;
    }

    @Override
    public String getStatus() {
        return "Cash Available: $" + getCashLevel() + " | Deposits Held: $" + totalDeposits +
               " | Bills: $10x" + bill10Count + " $20x" + bill20Count + 
               " $50x" + bill50Count + " $100x" + bill100Count;
    }

    @Override
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String newVersion) {
        firmwareVersion = newVersion;
    }
}
