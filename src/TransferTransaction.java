public class TransferTransaction implements ITransaction {
    private final AccountV1 source;
    private final AccountV1 destination;
    private final int amount;
    private final BankV1 bank;
    private final IReceiptService receiptService;
    private final ICashDispenser cashDispenser;
    private final IPrinterSupplies printerSupplies;
    private final IMaintainable printerFirmware;
    private final IPersistence persistence;

    public TransferTransaction(AccountV1 source, AccountV1 destination, int amount, BankV1 bank,
                               IReceiptService receiptService, ICashDispenser cashDispenser,
                               IPrinterSupplies printerSupplies, IMaintainable printerFirmware,
                               IPersistence persistence) {
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.bank = bank;
        this.receiptService = receiptService;
        this.cashDispenser = cashDispenser;
        this.printerSupplies = printerSupplies;
        this.printerFirmware = printerFirmware;
        this.persistence = persistence;
    }

    @Override
    public boolean execute() {
        if (!bank.authenticateTransfer(source, destination, amount)) {
            return false;
        }

        source.withdraw(amount);
        destination.deposit(amount);
        receiptService.printReceipt("Transfer");
        persistence.saveAccount(source);
        persistence.saveAccount(destination);
        persistence.logTransaction("TRANSFER", amount);
        persistence.updateInventory(cashDispenser, printerSupplies, printerFirmware);
        return true;
    }
}
