import java.time.LocalDate;

public class Interaction {
    private int interaction_id;
    private User user;
    private Article article;
    private String interactionType; // e.g., "Read", "Like", "Skip"
    private LocalDate interactionDate;

    public Interaction(int interaction_id, User user, Article article, String interactionType, LocalDate date){
        this.interaction_id = interaction_id;
        this.interactionDate = date;
        this.interactionType = interactionType;
        this.user = user;
        this.article = article;
    }

    public int getInteraction_id() {
        return interaction_id;
    }

    public void setInteraction_id(int interaction_id) {
        this.interaction_id = interaction_id;
    }

    public LocalDate getInteractionDate() {
        return interactionDate;
    }

    public void setInteractionDate(LocalDate interactionDate) {
        this.interactionDate = interactionDate;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public User getUser() {
        return user;
    }

    public Article getArticle() {
        return article;
    }
}
