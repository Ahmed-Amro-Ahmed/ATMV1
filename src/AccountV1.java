public class AccountV1 {
    private final String username;
    private final String pin;
    private int balance;
    public String getPin() {
        return pin;
    }

    public AccountV1(String username, String pin, int startingBalance) {
        this.username = username;
        this.pin = pin;
        this.balance = startingBalance;
    }

    public String getUsername() {
        return username;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public boolean authenticate(String pin) {
        return this.pin.equals(pin);
    }

    public boolean transfer(int amount, AccountV1 targetAccount) {
        if (amount > 0 && balance >= amount) {
            this.withdraw(amount);
            targetAccount.deposit(amount);
            return true;
        }
        return false;
    }
}
