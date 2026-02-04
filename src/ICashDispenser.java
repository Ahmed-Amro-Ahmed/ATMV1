public interface ICashDispenser extends IInventoryTracker {
    boolean hasCash(int amount);
    boolean dispense(int amount);
    void deposit(int amount);
    int getCashLevel();
    int getTotalDeposits();
    int getBill10Count();
    int getBill20Count();
    int getBill50Count();
    int getBill100Count();
}
