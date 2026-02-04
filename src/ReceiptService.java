public class ReceiptService implements IReceiptService {
    private final IPrinter printer;

    public ReceiptService(IPrinter printer) {
        this.printer = printer;
    }

    @Override
    public void printReceipt(String content) {
        printer.printReceipt(content);
    }
}
