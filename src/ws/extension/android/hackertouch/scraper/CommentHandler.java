package ws.extension.android.hackertouch.scraper;

public interface CommentHandler {
    void comment(int id,
                 int replyto,
                 int nesting,
                 String auth,
                 int score,
                 String author,
                 String time,
                 String body);
}
