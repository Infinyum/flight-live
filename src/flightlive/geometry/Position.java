package flightlive.geometry;

public final class Position {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    private final float latitude;
    private final float longitude;
    private final float speed;


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- CONSTRUCTOR ---------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /**
     * Default constructor for this class which instantiates the position-related attributes
     * and sets the speed to -1
     * @param lat a given latitude
     * @param lon a given longitude
     */
    public Position(float lat, float lon) {
        latitude = lat;
        longitude = lon;
        speed = -1;
    }


    /**
     * Constructor which instantiates all of this class' attributes
     * @param lat a given latitude
     * @param lon a given longitude
     * @param spd a given speed
     */
    public Position(float lat, float lon, float spd) {
        latitude = lat;
        longitude = lon;
        speed = spd;
    }


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /**
     * Getter for the latitude
     * @return this object's latitude attribute
     */
    public float getLatitude() {
        return latitude;
    }


    /**
     * Getter for the longitude
     * @return this object's longitude attribute
     */
    public float getLongitude() {
        return longitude;
    }


    /**
     * Getter for the speed
     * @return this object's speed attribute
     */
    public float getSpeed() { return speed; }

    // Override ----------------------------------------------

    /**
     * toString override
     * @return a decent way to display this object's attributes
     */
    @Override
    public String toString() {
        return "LATITUDE: " + latitude + ", LONGITUDE: " + longitude
                + "\nSPEED: " + speed;
    }
}