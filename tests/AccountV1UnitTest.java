public class AccountV1UnitTest {
    public static void main(String[] args) {
        AccountV1 account = new AccountV1("alice", "1234", 100);
        assertTrue(account.authenticate("1234"), "Expected authentication to succeed");
        assertFalse(account.authenticate("9999"), "Expected authentication to fail");

        account.deposit(50);
        assertEquals(150, account.getBalance(), "Balance after deposit");

        account.withdraw(20);
        assertEquals(130, account.getBalance(), "Balance after withdrawal");

        AccountV1 target = new AccountV1("bob", "0000", 10);
        assertTrue(account.transfer(30, target), "Expected transfer to succeed");
        assertEquals(100, account.getBalance(), "Source balance after transfer");
        assertEquals(40, target.getBalance(), "Target balance after transfer");

        assertFalse(account.transfer(1000, target), "Expected transfer to fail for insufficient funds");
        System.out.println("AccountV1UnitTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected=" + expected + " actual=" + actual);
        }
    }
}
