public class MainV1 {
    public static void main(String[] args) {
        System.setProperty("atm.version", PersistenceManager.loadSystemVersion());
        ICashDispenser cashBin = new CashBinV1(1000);
        PrinterV1 printer = new PrinterV1();
        BankV1 bank = new BankV1(PersistenceManager.loadAccounts());
        ConsoleUI ui = new ConsoleUI();

        IPersistence persistence = new PersistenceServiceV1();
        ATM atm = new ATM(cashBin, printer, printer, printer, bank, ui, persistence);
        atm.start();
    }
}
