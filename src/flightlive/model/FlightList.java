package flightlive.model;

/**
 * This class is useful for the JSON request. It stores the list of flights corresponding
 * to the filters as well as a timestamp for further queries.
 */
public final class FlightList {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    private Flight[] acList; // List of flights/planes
    private  String lastDv; // Timestamp for further query


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /**
     * Getter for the acList attribute
     * @return this object's acList attribute
     */
    public Flight[] getAcList() { return acList; }


    /**
     * Getter for the lastDv attribute
     * @return this object's lastDv attribute
     */
    public String getLastDv() { return lastDv; }


    /**
     * Setter for the acList attribute
     * @param acList the list of flights to set
     */
    public void setAcList(Flight[] acList) { this.acList = acList; }


    /**
     * Setter for the lastDv attribute
     * @param lastDv the timestamp to set
     */
    public void setLastDv(String lastDv) { this.lastDv = lastDv; }


    /**
     * Looks for the flight with the given id in this FlightList object
     * @param id the id of the flight
     * @return Flight object if found, null otherwise
     */
    public Flight getFlightById(int id) {
        for (Flight f : acList) {
            if (f.Id == id)
                return f;
        }
        return null;
    }


    // Override ----------------------------------------------

    /**
     * toString override to display the list of flights properly
     * @return the list of flights spaced properly
     */
    @Override
    public String toString() {
        String res = "";
        for (Flight f : getAcList()) {
            res += f.toString() + "\n\n";
        }
        return res;
    }
}
