import java.util.Arrays;

public class BankV1UnitTest {
    public static void main(String[] args) {
        AccountV1 alice = new AccountV1("alice", "1111", 100);
        AccountV1 bob = new AccountV1("bob", "2222", 50);
        BankV1 bank = new BankV1(Arrays.asList(alice, bob));

        assertFalse(bank.authenticateWithdrawal(null, 10), "Withdrawal should fail for null account");
        assertFalse(bank.authenticateWithdrawal(alice, -10), "Withdrawal should fail for invalid amount");
        assertFalse(bank.authenticateWithdrawal(alice, 15), "Withdrawal should fail for invalid denomination");
        assertFalse(bank.authenticateWithdrawal(alice, 200), "Withdrawal should fail for insufficient funds");
        assertTrue(bank.authenticateWithdrawal(alice, 20), "Withdrawal should succeed for valid amount");

        assertFalse(bank.authenticateTransfer(alice, alice, 10), "Transfer should fail to same account");
        assertFalse(bank.authenticateTransfer(alice, bob, 25), "Transfer should fail for invalid denomination");
        assertTrue(bank.authenticateTransfer(alice, bob, 20), "Transfer should succeed for valid amount");

        System.out.println("BankV1UnitTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }
}
