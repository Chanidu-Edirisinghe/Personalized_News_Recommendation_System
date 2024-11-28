import java.util.ArrayList;
import java.util.List;
public class Article {
    private int articleID;
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

    public int getArticleID() {
        return articleID;
    }

    public String getContent() {
        return content;
    }
}
