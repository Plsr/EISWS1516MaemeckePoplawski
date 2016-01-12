package de.rfunk.hochschulify.pojo;

/**
 * Created by Timo Maemecke (@timomeh) on 12/01/16.
 * <p/>
 * TODO: Add a class header comment
 */
public class User {
    private String mName;
    private String mId;
    private boolean mVerified;
    private String mLink;

    public User(String id, String name, boolean verified, String link) {
        mName = name;
        mId = id;
        mVerified = verified;
        mLink = link;
    }

    public User() {
        mName = "";
        mId = "";
        mVerified = false;
        mLink = "";
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public boolean isVerified() {
        return mVerified;
    }

    public void setVerified(boolean verified) {
        mVerified = verified;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }
}
