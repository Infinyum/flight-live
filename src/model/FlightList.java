package model;

public class FlightList {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    private Flight[] acList; //List of planes
    private  String lastDv; //Timestamp for further query

    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ------------------------------- CONSTRUCTORS ---------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- OPERATIONS ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    public Flight[] getAcList() {
        return acList;
    }

    public void setAcList(Flight[] acList) {
        this.acList = acList;
    }

    public String getLastDv() {
        return lastDv;
    }

    public void setLastDv(String lastDv) {
        this.lastDv = lastDv;
    }

    @Override
    public String toString() {
        String res = "";
        for (Flight f : getAcList()) {
            res += f.toString() + "\n\n";
        }
        return res;
    }
}
