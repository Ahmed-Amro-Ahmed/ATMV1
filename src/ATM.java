import java.util.ArrayList;
import java.util.List;

public class ATM {
    private static final String TECH_USER = "tech";
    private static final String TECH_PASS = "1234";

    private final ICashDispenser cashDispenser;
    private final IPrinter printer;
    private final IPrinterSupplies printerSupplies;
    private final IMaintainable printerFirmware;
    private final BankV1 bank;
    private final ConsoleUI ui;
    private final IReceiptService receiptService;
    private final IPersistence persistence;

    public ATM(ICashDispenser dispenser, IPrinter printer, IPrinterSupplies printerSupplies,
               IMaintainable printerFirmware, BankV1 bank, ConsoleUI ui, IPersistence persistence) {
        this.cashDispenser = dispenser;
        this.printer = printer;
        this.printerSupplies = printerSupplies;
        this.printerFirmware = printerFirmware;
        this.bank = bank;
        this.ui = ui;
        this.receiptService = new ReceiptService(printer);
        this.persistence = persistence;
        PersistenceManager.setInventoryTracker(dispenser, printerSupplies, printerFirmware);
    }

    public void start() {
        ui.displayMessage("Welcome to SOLID Bank");
        while (true) {
            showMainMenu();
            int choice = ui.promptInt("Select option: ");

            switch (choice) {
                case 0:
                    ui.displayMessage("ATM shutting down.");
                    return;
                case 1:
                    AccountV1 account = authenticateCustomer();
                    if (account != null) {
                        runCustomerSession(account);
                    }
                    break;
                case 2:
                    runTechnicianSession();
                    break;
                default:
                    ui.displayMessage("Invalid selection.");
            }
        }
    }

    private void runCustomerSession(AccountV1 account) {
        while (true) {
            showCustomerMenu();
            int choice = ui.promptInt("Select option: ");

            switch (choice) {
                case 0:
                    return;
                case 1:
                    ui.displayMessage("Balance: $" + account.getBalance());
                    break;
                case 2:
                    handleWithdrawal(account);
                    break;
                case 3:
                    if (processDeposit(account)) {
                        return;
                    }
                    break;
                case 4:
                    handleTransfer(account);
                    break;
                default:
                    ui.displayMessage("Invalid selection.");
            }
        }
    }

    private void runTechnicianSession() {
        String username = ui.promptString("Technician username: ");
        String password = ui.promptString("Technician password: ");

        if (!TECH_USER.equals(username) || !TECH_PASS.equals(password)) {
            ui.displayMessage("Access denied.");
            return;
        }

        List<IStatusView> techView = new ArrayList<>();
        if (cashDispenser instanceof IStatusView) {
            techView.add((IStatusView) cashDispenser);
        }
        if (printer instanceof IStatusView) {
            techView.add((IStatusView) printer);
        }

        TechnicianV1 tech = new TechnicianV1(techView);
        tech.checkInventory();
    }

    private void handleWithdrawal(AccountV1 account) {
        int amount = ui.promptInt("Enter amount: ");

        ITransaction withdrawal = new WithdrawalTransaction(
                account,
                amount,
                bank,
                cashDispenser,
                printerSupplies,
                printerFirmware,
                receiptService,
                persistence
        );

        if (withdrawal.execute()) {
            ui.displayMessage("Please take your cash.");
        } else {
            ui.displayMessage("Transaction failed.");
        }
    }

    private boolean processDeposit(AccountV1 account) {
        int amount = ui.promptInt("Deposit amount (multiples of $10, $20, $50, $100 only): ");

        ITransaction deposit = new DepositTransaction(
                account,
                amount,
                bank,
                receiptService,
                cashDispenser,
                printerSupplies,
                printerFirmware,
                persistence
        );
        if (deposit.execute()) {
            ui.displayMessage("Deposit successful.");
            return true;
        }

        ui.displayMessage("Transaction failed.");
        return false;
    }

    private void showMainMenu() {
        ui.displayMessage("\n=== ATM MAIN MENU ===");
        ui.displayMessage("1) Customer");
        ui.displayMessage("2) Technician");
        ui.displayMessage("0) Exit");
    }

    private void showCustomerMenu() {
        ui.displayMessage("\n--- CUSTOMER MENU ---");
        ui.displayMessage("1) Balance");
        ui.displayMessage("2) Withdraw");
        ui.displayMessage("3) Deposit");
        ui.displayMessage("4) Transfer");
        ui.displayMessage("0) Back");
    }

    private AccountV1 authenticateCustomer() {
        String username = ui.promptString("Enter username: ");
        String pin = ui.promptString("Enter PIN: ");
        for (AccountV1 acc : bank.getAccounts()) {
            if (acc.getUsername().equals(username) && acc.authenticate(pin)) {
                ui.displayMessage("Authentication successful.");
                return acc;
            }
        }
        ui.displayMessage("Authentication failed.");
        return null;
    }

    private void handleTransfer(AccountV1 currentAccount) {
        String targetUser = ui.promptString("Enter target username: ");
        AccountV1 targetAcc = bank.findAccount(targetUser);
        int amount = ui.promptInt("Enter amount: ");

        ITransaction transfer = new TransferTransaction(
                currentAccount,
                targetAcc,
                amount,
                bank,
                receiptService,
                cashDispenser,
                printerSupplies,
                printerFirmware,
                persistence
        );

        if (transfer.execute()) {
            ui.displayMessage("Transfer Successful!");
        } else {
            ui.displayMessage("Transfer Failed.");
        }
    }
}
