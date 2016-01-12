package de.rfunk.hochschulify.POJO;

/**
 * Created by Timo Maemecke (@timomeh) on 12/01/16.
 */
public class Entry {
    private String mTitle;
    private String mText;
    private String mAuthor;
    private int mSubCount;

    public Entry(String title, String text, String author, int subCount) {
        mTitle = title;
        mText = text;
        mAuthor = author;
        mSubCount = subCount;
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

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public Integer getSubCount() {
        return mSubCount;
    }

    public void setSubCount(int subCount) {
        mSubCount = subCount;
    }
}
