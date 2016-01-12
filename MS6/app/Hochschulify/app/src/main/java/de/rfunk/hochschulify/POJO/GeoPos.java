package de.rfunk.hochschulify.pojo;

/**
 * Created by Timo Maemecke (@timomeh) on 12/01/16.
 * <p/>
 * TODO: Add a class header comment
 */
public class GeoPos {
    private double mLat;
    private double mLong;

    public GeoPos(double lat, double aLong) {
        mLat = lat;
        mLong = aLong;
    }

    public GeoPos() {
        mLat = 0;
        mLong = 0;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLong() {
        return mLong;
    }

    public void setLong(double aLong) {
        mLong = aLong;
    }
}
