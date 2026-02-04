import java.io.*;
import java.util.*;
import org.json.*;

public class PersistenceManager {
    private static final String USERS_FILE = "V1/users_secure.json";
    private static final String TRANSACTIONS_FILE = "V1/transactions.json";
    private static final String INVENTORY_FILE = "V1/inventory.json";
    private static IInventoryTracker inventoryTracker;
    private static IPrinterSupplies printerSupplies;
    private static IMaintainable printerFirmware;

    public static void setInventoryTracker(IInventoryTracker tracker, IPrinterSupplies supplies, IMaintainable maintainable) {
        inventoryTracker = tracker;
        printerSupplies = supplies;
        printerFirmware = maintainable;
    }

    public static List<AccountV1> loadAccounts() {
        List<AccountV1> accounts = new ArrayList<>();
        try {
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(USERS_FILE)));
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String username = obj.getString("user");
                String pin = obj.getString("pass");
                int balance = obj.has("balance") ? obj.getInt("balance") : 1000;
                accounts.add(new AccountV1(username, pin, balance));
            }
        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return accounts;
    }

    public static void saveAccounts(List<AccountV1> accounts) {
        JSONArray arr = new JSONArray();
        for (AccountV1 acc : accounts) {
            JSONObject obj = new JSONObject();
            obj.put("user", acc.getUsername());
            obj.put("pass", acc.getPin());
            obj.put("balance", acc.getBalance());
            obj.put("role", "USER"); // or ADMIN if you want to distinguish
            arr.put(obj);
        }
        try (FileWriter file = new FileWriter(USERS_FILE)) {
            file.write(arr.toString(2));
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

        public static void updateInventory(int cashLevel, int depositsHeld, int paperLevel, int inkLevel, String firmware) {
        try {
            org.json.JSONObject obj = new org.json.JSONObject();
            obj.put("cashLevel", cashLevel);
            obj.put("depositsHeld", depositsHeld);
            obj.put("paperLevel", paperLevel);
            obj.put("inkLevel", inkLevel);
            obj.put("firmware", firmware);
            obj.put("lastUpdated", java.time.Instant.now().toString());
            try (FileWriter fw = new FileWriter(INVENTORY_FILE)) {
                fw.write(obj.toString(2));
            }
        } catch (Exception e) {
            System.out.println("Error updating inventory: " + e.getMessage());
        }
    }

    public static void logTransaction(String type, int amount, String result, String user) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", type);
            obj.put("amount", amount);
            obj.put("result", result);
            obj.put("timestamp", java.time.Instant.now().toString());
            if (user != null) obj.put("user", user);
            FileServiceV1.appendTransactionToJsonArray("transactions.json", obj.toString());
        } catch (Exception e) {
            System.out.println("Error logging transaction: " + e.getMessage());
        }
    }

    public static void saveDeposit(AccountV1 account, int amount) {
        List<AccountV1> accounts = loadAccounts();
        for (AccountV1 acc : accounts) {
            if (acc.getUsername().equals(account.getUsername())) {
                int delta = account.getBalance() - acc.getBalance();
                if (delta > 0) {
                    acc.deposit(delta);
                } else if (delta < 0) {
                    acc.withdraw(-delta);
                }
                break;
            }
        }
        saveAccounts(accounts);
        logTransaction("DEPOSIT", amount, "SUCCESS", account.getUsername());
        if (inventoryTracker != null && printerSupplies != null && printerFirmware != null) {
            updateInventory(inventoryTracker, printerSupplies, printerFirmware);
        }
    }

    public static void saveTransfer(AccountV1 source, AccountV1 destination, int amount) {
        List<AccountV1> accounts = loadAccounts();
        for (AccountV1 acc : accounts) {
            if (acc.getUsername().equals(source.getUsername())) {
                int delta = source.getBalance() - acc.getBalance();
                if (delta > 0) {
                    acc.deposit(delta);
                } else if (delta < 0) {
                    acc.withdraw(-delta);
                }
            } else if (acc.getUsername().equals(destination.getUsername())) {
                int delta = destination.getBalance() - acc.getBalance();
                if (delta > 0) {
                    acc.deposit(delta);
                } else if (delta < 0) {
                    acc.withdraw(-delta);
                }
            }
        }
        saveAccounts(accounts);
        logTransaction("TRANSFER", amount, "TO_" + destination.getUsername(), source.getUsername());
        if (inventoryTracker != null && printerSupplies != null && printerFirmware != null) {
            updateInventory(inventoryTracker, printerSupplies, printerFirmware);
        }
    }

    public static void saveWithdrawal(AccountV1 account, int amount) {
        logTransaction("WITHDRAW", amount, "SUCCESS", account.getUsername());
        if (inventoryTracker != null && printerSupplies != null && printerFirmware != null) {
            updateInventory(inventoryTracker, printerSupplies, printerFirmware);
        }
    }

    public static void updateInventory(IInventoryTracker cashBin, IPrinterSupplies supplies, IMaintainable printerFirmware) {
        try {
            JSONObject inv = new JSONObject();
            inv.put("cashLevel", cashBin.getCashLevel());
            inv.put("bill10Count", cashBin.getBill10Count());
            inv.put("bill20Count", cashBin.getBill20Count());
            inv.put("bill50Count", cashBin.getBill50Count());
            inv.put("bill100Count", cashBin.getBill100Count());
            inv.put("depositsHeld", cashBin.getTotalDeposits());
            inv.put("paperLevel", supplies.getPaperLevel());
            inv.put("inkLevel", supplies.getInkLevel());
            inv.put("firmware", System.getProperty("atm.version", "1.0"));
            inv.put("printerFirmware", printerFirmware.getFirmwareVersion());
            inv.put("lastUpdated", java.time.Instant.now().toString());
            FileServiceV1.saveToFile("inventory.json", inv.toString(2));
        } catch (Exception e) {
            System.out.println("Error updating inventory: " + e.getMessage());
        }
    }

    public static void updateInventory() {
        if (inventoryTracker != null && printerSupplies != null && printerFirmware != null) {
            updateInventory(inventoryTracker, printerSupplies, printerFirmware);
        }
    }

    public static void updateFullInventory(IInventoryTracker cashBin, IPrinterSupplies supplies, IMaintainable printerFirmware) {
        updateInventory(cashBin, supplies, printerFirmware);
    }

    public static String loadSystemVersion() {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(INVENTORY_FILE)));
            JSONObject obj = new JSONObject(content);
            if (obj.has("firmware")) {
                String raw = obj.getString("firmware");
                String normalized = normalizeVersionString(raw);
                if (normalized != null) {
                    return normalized;
                }
            }
        } catch (Exception e) {
            // Ignore, use default
        }
        return "1.0";
    }

    private static String normalizeVersionString(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        StringBuilder numeric = new StringBuilder();
        for (int i = 0; i < trimmed.length(); i++) {
            char ch = trimmed.charAt(i);
            if ((ch >= '0' && ch <= '9') || ch == '.') {
                numeric.append(ch);
            } else {
                break;
            }
        }
        if (numeric.length() == 0) {
            return null;
        }
        try {
            Double.parseDouble(numeric.toString());
            return numeric.toString();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
