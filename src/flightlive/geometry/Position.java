package flightlive.geometry;

public final class Position {
    private final float latitude;
    private final float longitude;

    public Position(float lat, float lon) {
        latitude = lat;
        longitude = lon;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}