package flightlive.model;


/**
 * This class represents an airport. It has access to the city in which it is located.
 */
public final class Airport {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    private String name;
    private City city;
    private String icao;
    private double latitude;
    private double longitude;


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- CONSTRUCTOR ---------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /**
     * Constructor for an Airport object
     * @param n: name of the airport
     * @param c: city where the airport is located
     * @param i: ICAO code of the airport
     * @param lat: latitude of the airport
     * @param lon: longitude of the airport
     */
    public Airport(String n, City c, String i, double lat, double lon) {
        name = n; city = c; icao = i; latitude = lat; longitude = lon;
    }


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */


    /**
     * A shorter version of toString suitable for lists of airports
     * @return a short string displaying only the name and the country of this airport
     */
    public String toStringShort() {
        return "AIRPORT " + name + " (" + city.getCountry().getName() + ")";
    }


    /**
     * Getter for the name attribute
     * @return the name of this airport
     */
    public String getName() { return name; }


    /**
     * Getter for the city attribute
     * @return this city object in which this airport is located
     */
    public City getCity() { return city; }


    /**
     * Getter for the icao attribute
     * @return the icao code of this airport
     */
    public String getIcao() { return icao; }


    /**
     * Getter for the latitude attribute
     * @return the latitude of this airport
     */
    public double getLatitude() { return latitude; }


    /**
     * Getter for the longitude attribute
     * @return the longitude of this airport
     */
    public double getLongitude() { return longitude; }


    // Override ----------------------------------------------

    /**
     * toString override
     * @return a decent way of displaying an Airport object's infos
     */
    @Override
    public String toString() {
        return "AIRPORT " + name + " in " + city + " (" + city.getCountry().getName() + ")" +
                "\nICAO: " + icao + "\nPOSITION: " + latitude + ", " + longitude;
    }
}
