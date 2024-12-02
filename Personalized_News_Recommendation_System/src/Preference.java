public class Preference {
    private Category category;
    private int interestLevel;//(0 to 100)

    public Preference(Category category, int interestLevel){
        this.category = category;
        this.interestLevel = interestLevel;
    }

    public Category getCategory() {
        return category;
    }

    public int getInterestLevel() {
        return interestLevel;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setInterestLevel(int interestLevel) {
        this.interestLevel = interestLevel;
    }
}
