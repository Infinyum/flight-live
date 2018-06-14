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
    public boolean Mil;
    public float Spd;
    public float Trak;
    public float Alt;
    public String Type;
    public String Op;
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

    /**
     * toString override: displays a lot of information about this Flight object
     * @return the string containing this information
     */
    @Override
    public String toString() {
        return "FLIGHT'S ID: " + Id + "\nFROM: " + From + "\nTO: " + To + "\nAIRCRAFT TYPE: "
                + Type + ", MILITARY: " + (Mil ? "yes" : "no") + "\nSPEED: " + Spd
                + ", ALTITUDE: " + Alt;
    }


    /**
     * An alternative to the toString method with a fewer amount of information
     * @param departureCity the name of the departure city
     * @param arrivalCity the name of the departure city
     * @return the string containing this information
     */
    public String toStringShort(String departureCity, String arrivalCity) {
        return "ID: " + Id + "\tAIRLINE: " + Op + "\nFROM: " + departureCity
                + "\tTO: " + arrivalCity;
    }


    /**
     * equals override: it only compares the flights' id on purpose
     * @param o the object to compare this Flight with
     * @return true if these are considered equals, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Id == flight.Id;
    }
}
