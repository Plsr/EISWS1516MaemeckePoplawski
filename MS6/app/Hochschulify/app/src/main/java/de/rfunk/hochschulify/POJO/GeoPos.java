package de.rfunk.hochschulify.pojo;

/**
 * Created by Timo Maemecke (@timomeh) on 12/01/16.
 * <p/>
 * TODO: Add a class header comment
 */
public class GeoPos {
    private float mLat;
    private float mLong;

    public GeoPos(float lat, float aLong) {
        mLat = lat;
        mLong = aLong;
    }

    public float getLat() {
        return mLat;
    }

    public void setLat(float lat) {
        mLat = lat;
    }

    public float getLong() {
        return mLong;
    }

    public void setLong(float aLong) {
        mLong = aLong;
    }
}
