package flightlive.model;

/**
 * This class represents a single flight. It is automatically created when executing a JSON
 * request. The name of the attributes has to match the name of the JSON variables.
 */
public final class Flight {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    public int Id;
    public String Icao;
    public float Spd;
    public float Trak;
    public String Type;
    public float Lat;
    public float Long;
    public String From;
    public String To;
    public double[] Cot;

    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- CONSTRUCTOR ---------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    // The objects of this class are automatically created during the JSON request


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */


    /**
     * Getter of the icao attribute
     * @return this object's icao attribute
     */
    public String getIcao() { return Icao; }


    /**
     * Getter of the cot attribute
     * @return this object's cot attribute
     */
    public double[] getCot() { return Cot; }


    // Override ----------------------------------------------

    @Override
    public String toString() {
        return "FLIGHT NUMBER " + Id + ", ICAO:" + "\nLAT: " + Lat + ", LON: " + Long;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Id == flight.Id;
    }
}
