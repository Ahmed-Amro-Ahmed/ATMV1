import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class DepositTransactionIntegrationTest {
    public static void main(String[] args) {
        String usersPath = "V1/users_secure.json";
        String transactionsPath = "V1/transactions.json";
        String inventoryPath = "V1/inventory.json";

        String usersBackup = TestFileUtils.readFileOrNull(usersPath);
        String transactionsBackup = TestFileUtils.readFileOrNull(transactionsPath);
        String inventoryBackup = TestFileUtils.readFileOrNull(inventoryPath);

        try {
            AccountV1 account = new AccountV1("alice", "1234", 100);
            List<AccountV1> accounts = Arrays.asList(account);
            PersistenceManager.saveAccounts(accounts);
            TestFileUtils.writeFile(transactionsPath, "[\n]\n");
            TestFileUtils.writeFile(inventoryPath, "{}\n");

            BankV1 bank = new BankV1(accounts);
            CashBinV1 cashBin = new CashBinV1(10000);
            PrinterV1 printer = new PrinterV1();
            ReceiptService receiptService = new ReceiptService(printer);
            PersistenceServiceV1 persistence = new PersistenceServiceV1();

            DepositTransaction transaction = new DepositTransaction(
                    account,
                    50,
                    bank,
                    receiptService,
                    cashBin,
                    printer,
                    printer,
                    persistence
            );

            assertTrue(transaction.execute(), "Deposit transaction should succeed");
            assertEquals(150, account.getBalance(), "Account balance should increase after deposit");

            String updatedUsers = TestFileUtils.readFileOrNull(usersPath);
            JSONArray usersArray = new JSONArray(updatedUsers);
            assertEquals(1, usersArray.length(), "Expected one user in users file");
            JSONObject user = usersArray.getJSONObject(0);
            assertEquals(150, user.getInt("balance"), "Persisted balance should match account");

            String updatedTransactions = TestFileUtils.readFileOrNull(transactionsPath);
            JSONArray transactionsArray = new JSONArray(updatedTransactions);
            assertTrue(transactionsArray.length() >= 1, "Expected at least one transaction entry");
            JSONObject lastTransaction = transactionsArray.getJSONObject(transactionsArray.length() - 1);
            assertEquals("DEPOSIT", lastTransaction.getString("type"), "Transaction type should be DEPOSIT");
            assertEquals(50, lastTransaction.getInt("amount"), "Transaction amount should match deposit");

            System.out.println("DepositTransactionIntegrationTest passed");
        } finally {
            TestFileUtils.restoreOrDelete(usersPath, usersBackup);
            TestFileUtils.restoreOrDelete(transactionsPath, transactionsBackup);
            TestFileUtils.restoreOrDelete(inventoryPath, inventoryBackup);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected=" + expected + " actual=" + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected=" + expected + " actual=" + actual);
        }
    }
}
