public class WithdrawalTransaction implements ITransaction {
    private final AccountV1 account;
    private final int amount;
    private final BankV1 bank;
    private final ICashDispenser cashBin;
    private final IPrinterSupplies printerSupplies;
    private final IMaintainable printerFirmware;
    private final IReceiptService receiptService;
    private final IPersistence persistence;

    public WithdrawalTransaction(AccountV1 account, int amount, BankV1 bank, ICashDispenser cashBin,
                                 IPrinterSupplies printerSupplies, IMaintainable printerFirmware,
                                 IReceiptService receiptService, IPersistence persistence) {
        this.account = account;
        this.amount = amount;
        this.bank = bank;
        this.cashBin = cashBin;
        this.printerSupplies = printerSupplies;
        this.printerFirmware = printerFirmware;
        this.receiptService = receiptService;
        this.persistence = persistence;
    }

    @Override
    public boolean execute() {
        if (!bank.authenticateWithdrawal(account, amount)) {
            return false;
        }

        if (!cashBin.hasCash(amount)) {
            System.out.println("ATM has insufficient cash.");
            return false;
        }

        if (!cashBin.dispense(amount)) {
            return false;
        }

        account.withdraw(amount);
        receiptService.printReceipt("Withdrawal");
        saveSystemState();
        return true;
    }

    private void saveSystemState() {
        persistence.saveAccount(account);
        persistence.logTransaction("WITHDRAW", amount);
        persistence.updateInventory(cashBin, printerSupplies, printerFirmware);
    }
}
