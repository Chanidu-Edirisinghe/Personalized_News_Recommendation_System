public class Preference {
    private Category category;
    private int interestLevel;
    private User user;

    // constructor
    public Preference(Category category, int interestLevel, User user){
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
