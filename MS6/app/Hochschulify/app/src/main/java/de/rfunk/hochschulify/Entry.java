package de.rfunk.hochschulify;

/**
 * Created by Timo Maemecke (@timomeh) on 12/01/16.
 */
public class Entry {
    private String mTitle;
    private String mText;
    private String mAuthor;
    private int mSubCount;
    private String mId;



    public Entry(String title, String text, String author, int subCount, String id) {
        mTitle = title;
        mText = text;
        mAuthor = author;
        mSubCount = subCount;
        mId = id;

    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
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
