import java.util.ArrayList;
import java.util.List;
public class Article {
    private int articleID;
    private String title;
    private String content;
    private Category category;


    public Article(int articleID, String title, String content, Category category){
        this.articleID = articleID;
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void displayArticle() {
        // Calculate dynamic width
        int titleLength = Math.max(title.length(), "ARTICLE".length());
        int borderLength = Math.max(titleLength + 10, 120); // Minimum width of 40

        String border = "=".repeat(borderLength);
        String separator = "-".repeat(borderLength);

        // Display the article details
        System.out.println(border + "\n");
        System.out.println(centerText(title, borderLength));
        System.out.println("\n" + border);
        System.out.println(wrapText(content, borderLength)); // Wrap content to fit
        System.out.println("\n"+border+"\n");
    }

    // Helper method to center-align text
    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    // Helper method to wrap text
    private String wrapText(String text, int width) {
        StringBuilder wrappedText = new StringBuilder();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + width, text.length());
            wrappedText.append(text, start, end).append("\n");
            start = end;
        }
        return wrappedText.toString().trim(); // Remove trailing newline
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

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArticleID(int articleID) {
        this.articleID = articleID;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
