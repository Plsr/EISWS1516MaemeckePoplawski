package de.rfunk.hochschulify.utils;

import org.json.JSONException;
import org.json.JSONObject;

import de.rfunk.hochschulify.pojo.Course;
import de.rfunk.hochschulify.pojo.Entry;
import de.rfunk.hochschulify.pojo.GeoPos;
import de.rfunk.hochschulify.pojo.University;
import de.rfunk.hochschulify.pojo.User;

/**
 * Created by Timo Maemecke (@timomeh) on 12/01/16.
 * <p/>
 * Helper class to parse JSONObjects to POJOS
 */
public class Parse {

    /*
    Everything in here is coded the same way. Just entry is commented.
     */

    public static final Entry entry(JSONObject entry) {
        // Create a new Entry-POJO, check if the JSONObject has the specified keys
        // and set them to the POJO.
        // Then return it.
        Entry retEntry = new Entry();
        try {
            if (entry.has("title"))
                retEntry.setTitle(entry.getString("title"));
            if (entry.has("text"))
                retEntry.setText(entry.getString("text"));
            if (entry.has("user"))
                retEntry.setAuthor(user(entry.getJSONObject("user")));
            if (entry.has("link")) {
                JSONObject link = entry.getJSONObject("link");
                if (link.has("self"))
                    retEntry.setLink(link.getString("self"));
            }
            if (entry.has("recommendation")) {
                retEntry.setHasRecommendation(true);
                retEntry.setRecommendation(entry.getBoolean("recommendation"));
            }
            if (entry.has("_id"))
                retEntry.setId(entry.getString("_id"));
            if (entry.has("type"))
                retEntry.setType(entry.getString("type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

       return retEntry;
    }

    public static final User user(JSONObject user) {
        User retUser = new User();
        try {
            if (user.has("name"))
                retUser.setName(user.getString("name"));
            if (user.has("_id"))
                retUser.setId(user.getString("_id"));
            if (user.has("verified"))
                retUser.setVerified(user.getBoolean("verified"));
            if (user.has("link")) {
                JSONObject link = user.getJSONObject("link");
                if (link.has("self"))
                    retUser.setLink(link.getString("self"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retUser;
    }

    public static final Course course(JSONObject course) {
        Course retCourse = new Course();
        try {
            if (course.has("name"))
                retCourse.setName(course.getString("name"));
            if (course.has("_id"))
                retCourse.setId(course.getString("_id"));
            if (course.has("university"))
                retCourse.setUniversity(university(course.getJSONObject("university")));
            if (course.has("recommendation"))
                retCourse.setRecommendation(course.getDouble("recommendation"));
            if (course.has("link")) {
                JSONObject link = course.getJSONObject("link");
                if (link.has("self"))
                    retCourse.setLink(link.getString("self"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retCourse;
    }

    public static final University university(JSONObject university) {
        University retUni = new University();

        try {
            if (university.has("name"))
                retUni.setName(university.getString("name"));
            if (university.has("position")) {
                JSONObject pos = university.getJSONObject("position");
                retUni.setGeoPos(new GeoPos(pos.getDouble("lat"), pos.getDouble("long")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return retUni;
    }
}
