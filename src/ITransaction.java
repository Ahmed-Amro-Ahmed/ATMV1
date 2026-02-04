public interface ITransaction {
    /**
     * @return true if the transaction was successful
     */
    boolean execute();
}
