import java.util.ArrayList;
import java.util.List;
public class Article {
    private String articleID;
    private String title;
    private String content;
    private Category category;
    private List<Keyword> keywords = new ArrayList<>();

    public void displayArticle(){

    }

    public String getTitle() {
        return title;
    }

    public Category getCategory() {
        return category;
    }

    public String getArticleID() {
        return articleID;
    }

    public String getContent() {
        return content;
    }
}
