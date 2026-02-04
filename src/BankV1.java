import java.util.List;

/**
 * Bank Entity - Authenticates and validates all transactions
 * Acts as the central authority for transaction approval
 */
public class BankV1 {
    private List<AccountV1> accounts;
    
    public BankV1(List<AccountV1> accounts) {
        this.accounts = accounts;
    }
    
    /**
     * Authenticate and validate a withdrawal transaction
     */
    public boolean authenticateWithdrawal(AccountV1 account, int amount) {
        if (account == null) {
            System.out.println("[BANK]: Authentication failed - Invalid account");
            return false;
        }
        
        if (amount <= 0) {
            System.out.println("[BANK]: Transaction denied - Invalid amount");
            return false;
        }
        
        if (!isValidDenomination(amount)) {
            System.out.println("[BANK]: Transaction denied - Invalid denomination");
            return false;
        }
        
        if (account.getBalance() < amount) {
            System.out.println("[BANK]: Transaction denied - Insufficient funds");
            return false;
        }
        
        System.out.println("[BANK]: Withdrawal authenticated - $" + amount);
        return true;
    }
    
    /**
     * Authenticate and validate a deposit transaction
     */
    public boolean authenticateDeposit(AccountV1 account, int amount) {
        if (account == null) {
            System.out.println("[BANK]: Authentication failed - Invalid account");
            return false;
        }
        
        if (amount <= 0) {
            System.out.println("[BANK]: Transaction denied - Invalid amount");
            return false;
        }
        
        if (!isValidDenomination(amount)) {
            System.out.println("[BANK]: Transaction denied - Invalid denomination");
            return false;
        }
        
        System.out.println("[BANK]: Deposit authenticated - $" + amount);
        return true;
    }
    
    /**
     * Authenticate and validate a transfer transaction
     */
    public boolean authenticateTransfer(AccountV1 fromAccount, AccountV1 toAccount, int amount) {
        if (fromAccount == null || toAccount == null) {
            System.out.println("[BANK]: Authentication failed - Invalid account(s)");
            return false;
        }
        
        if (fromAccount == toAccount) {
            System.out.println("[BANK]: Transaction denied - Cannot transfer to same account");
            return false;
        }
        
        if (amount <= 0) {
            System.out.println("[BANK]: Transaction denied - Invalid amount");
            return false;
        }
        
        if (!isValidDenomination(amount)) {
            System.out.println("[BANK]: Transaction denied - Invalid denomination");
            return false;
        }
        
        if (fromAccount.getBalance() < amount) {
            System.out.println("[BANK]: Transaction denied - Insufficient funds");
            return false;
        }
        
        System.out.println("[BANK]: Transfer authenticated - $" + amount + " to " + toAccount.getUsername());
        return true;
    }
    
    /**
     * Find account by username
     */
    public AccountV1 findAccount(String username) {
        for (AccountV1 acc : accounts) {
            if (acc.getUsername().equals(username)) {
                return acc;
            }
        }
        return null;
    }
    
    /**
     * Validate denomination (multiples of 10)
     */
    private boolean isValidDenomination(int amount) {
        return amount % 10 == 0;
    }
    
    /**
     * Update accounts list
     */
    public void updateAccounts(List<AccountV1> accounts) {
        this.accounts = accounts;
    }

    public List<AccountV1> getAccounts() {
        return accounts;
    }
}
