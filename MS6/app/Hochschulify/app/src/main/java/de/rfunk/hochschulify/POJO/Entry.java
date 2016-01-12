package de.rfunk.hochschulify.pojo;

/**
 * Created by Timo Maemecke (@timomeh) on 12/01/16.
 */
public class Entry {
    private String mTitle;
    private String mText;
    private User mAuthor;
    private int mSubCount;
    private String mLink;
    private String mId;

    public Entry(String title, String text, User author, int subCount, String link, String id) {
        mTitle = title;
        mText = text;
        mAuthor = author;
        mSubCount = subCount;
        mLink = link;
        mId = id;
    }

    public Entry() {
        mTitle = "";
        mText = "";
        mAuthor = new User();
        mSubCount = 0;
        mLink = "";
        mId = "";
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public User getAuthor() {
        return mAuthor;
    }

    public void setAuthor(User author) {
        mAuthor = author;
    }

    public Integer getSubCount() {
        return mSubCount;
    }

    public void setSubCount(int subCount) {
        mSubCount = subCount;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }
}