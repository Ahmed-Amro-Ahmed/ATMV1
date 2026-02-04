public interface IInventoryTracker {
    int getCashLevel();
    int getTotalDeposits();
    int getBill10Count();
    int getBill20Count();
    int getBill50Count();
    int getBill100Count();
}
