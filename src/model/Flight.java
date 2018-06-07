package model;

import java.util.Objects;

public final class Flight {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* --------------------------------- ATTRIBUTS ----------------------------------- */
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

    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ------------------------------- CONSTRUCTEURS --------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /* /////////////////////////////////////////////////////////////////////////////// */
    /* --------------------------------- METHODES- ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    public String getIcao() { return Icao; }

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
