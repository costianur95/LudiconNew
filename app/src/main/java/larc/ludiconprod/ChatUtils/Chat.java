package larc.ludiconprod.ChatUtils;

import larc.ludiconprod.Utils.util.DateManager;

/**
 * @author greg
 * @since 6/21/13
 */
public class Chat {

    private String message;
    private String author;
    private String authorID;
    public long date;
    public boolean seen;

    // Required default constructor for DatabaseReference object mapping
    @SuppressWarnings("unused")
    private Chat() {
    }

    public Chat(String message, String author, long date, String authorID) {
        this.message = message;
        this.author = author;
        this.date = date;
        this.seen = false;
        this.authorID = authorID;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorID() {
        return authorID;
    }
}
