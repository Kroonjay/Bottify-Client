package tasks.banking;

public class DepositAllBanking extends Banking {

    private String[] exceptItems;

    public DepositAllBanking(final String... exceptItems) {
        this.exceptItems = exceptItems;
    }

    public void setExceptItems(final String... exceptItems) {
        this.exceptItems = exceptItems;
    }

    @Override
    protected boolean bank() {
        if (exceptItems == null) {
            return getBank().depositAll();
        }
        return getBank().depositAllExcept(exceptItems);
    }
}
