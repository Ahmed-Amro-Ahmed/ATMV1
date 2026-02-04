import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class TransferTransactionIntegrationTest {
    public static void main(String[] args) {
        String usersPath = "V1/users_secure.json";
        String transactionsPath = "V1/transactions.json";
        String inventoryPath = "V1/inventory.json";

        String usersBackup = TestFileUtils.readFileOrNull(usersPath);
        String transactionsBackup = TestFileUtils.readFileOrNull(transactionsPath);
        String inventoryBackup = TestFileUtils.readFileOrNull(inventoryPath);

        try {
            AccountV1 alice = new AccountV1("alice", "1234", 200);
            AccountV1 bob = new AccountV1("bob", "5678", 50);
            List<AccountV1> accounts = Arrays.asList(alice, bob);
            PersistenceManager.saveAccounts(accounts);
            TestFileUtils.writeFile(transactionsPath, "[\n]\n");
            TestFileUtils.writeFile(inventoryPath, "{}\n");

            BankV1 bank = new BankV1(accounts);
            CashBinV1 cashBin = new CashBinV1(10000);
            PrinterV1 printer = new PrinterV1();
            ReceiptService receiptService = new ReceiptService(printer);
            PersistenceServiceV1 persistence = new PersistenceServiceV1();

            TransferTransaction transaction = new TransferTransaction(
                    alice,
                    bob,
                    70,
                    bank,
                    receiptService,
                    cashBin,
                    printer,
                    printer,
                    persistence
            );

            assertTrue(transaction.execute(), "Transfer transaction should succeed");
            assertEquals(130, alice.getBalance(), "Source balance should decrease after transfer");
            assertEquals(120, bob.getBalance(), "Destination balance should increase after transfer");

            String updatedUsers = TestFileUtils.readFileOrNull(usersPath);
            JSONArray usersArray = new JSONArray(updatedUsers);
            assertEquals(2, usersArray.length(), "Expected two users in users file");
            assertEquals(130, findBalance(usersArray, "alice"), "Persisted balance for alice");
            assertEquals(120, findBalance(usersArray, "bob"), "Persisted balance for bob");

            String updatedTransactions = TestFileUtils.readFileOrNull(transactionsPath);
            JSONArray transactionsArray = new JSONArray(updatedTransactions);
            assertTrue(transactionsArray.length() >= 1, "Expected at least one transaction entry");
            JSONObject lastTransaction = transactionsArray.getJSONObject(transactionsArray.length() - 1);
            assertEquals("TRANSFER", lastTransaction.getString("type"), "Transaction type should be TRANSFER");
            assertEquals(70, lastTransaction.getInt("amount"), "Transaction amount should match transfer");

            System.out.println("TransferTransactionIntegrationTest passed");
        } finally {
            TestFileUtils.restoreOrDelete(usersPath, usersBackup);
            TestFileUtils.restoreOrDelete(transactionsPath, transactionsBackup);
            TestFileUtils.restoreOrDelete(inventoryPath, inventoryBackup);
        }
    }

    private static int findBalance(JSONArray array, String username) {
        for (int i = 0; i < array.length(); i++) {
            JSONObject user = array.getJSONObject(i);
            if (username.equals(user.getString("user"))) {
                return user.getInt("balance");
            }
        }
        throw new AssertionError("User not found in users file: " + username);
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
