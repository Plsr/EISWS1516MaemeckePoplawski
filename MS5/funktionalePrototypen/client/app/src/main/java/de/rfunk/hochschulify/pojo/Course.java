package de.rfunk.hochschulify.pojo;

/**
 * Created by Timo Maemecke (@timomeh) on 12/01/16.
 * <p/>
 * TODO: Add a class header comment
 */
public class Course {
    private String mName;
    private University mUniversity;
    private String mLink;
    private String mId;

    public Course(String name, University university, String link, String id) {
        mName = name;
        mLink = link;
        mUniversity = university;
        mId = id;
    }

    public Course(String name, University university) {
        mName = name;
        mUniversity = university;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Course() {
        mName = "";
        mUniversity = new University();
        mLink = "";
        mId = "";
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public University getUniversity() {
        return mUniversity;
    }

    public void setUniversity(University university) {
        mUniversity = university;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }
}
