import java.util.List;

public class PersistenceServiceV1 implements IPersistence {
    @Override
    public void saveAccount(AccountV1 account) {
        List<AccountV1> accounts = PersistenceManager.loadAccounts();
        boolean found = false;
        for (AccountV1 acc : accounts) {
            if (acc.getUsername().equals(account.getUsername())) {
                int delta = account.getBalance() - acc.getBalance();
                if (delta > 0) {
                    acc.deposit(delta);
                } else if (delta < 0) {
                    acc.withdraw(-delta);
                }
                found = true;
                break;
            }
        }
        if (!found) {
            accounts.add(account);
        }
        PersistenceManager.saveAccounts(accounts);
    }

    @Override
    public void updateInventory(ICashDispenser dispenser, IPrinterSupplies printerSupplies, IMaintainable printerFirmware) {
        PersistenceManager.updateInventory(dispenser, printerSupplies, printerFirmware);
    }

    @Override
    public void logTransaction(String type, int amount) {
        PersistenceManager.logTransaction(type, amount, "SUCCESS", null);
    }
}
