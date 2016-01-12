package de.rfunk.hochschulify.pojo;

/**
 * Created by Timo Maemecke (@timomeh) on 12/01/16.
 * <p/>
 * TODO: Add a class header comment
 */
public class University {
    private String mName;
    private GeoPos mGeoPos;

    public University(String name) {
        mName = name;
        mGeoPos = new GeoPos();
    }

    public University() {
        mName = "";
        mGeoPos = new GeoPos();
    }

    public University(String name, GeoPos geoPos) {
        mName = name;
        mGeoPos = geoPos;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public GeoPos getGeoPos() {
        return mGeoPos;
    }

    public void setGeoPos(GeoPos geoPos) {
        mGeoPos = geoPos;
    }
}
