package model;

import java.util.ArrayList;

/**
 * This class represents a city. It has access to the country in which it is located
 * as well as all the airports in it.
 */
public final class City {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    private String name;
    private Country country;
    private ArrayList<Airport> airports;


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- CONSTRUCTOR ---------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /**
     * Constructor for a City object
     * @param n: name of the city
     * @param c: the country in which this city is located
     */
    public City(String n, Country c) {
        name = n; country = c; airports = new ArrayList<>();
    }


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */


    /**
     * Getter for the name attribute
     * @return the name of this city
     */
    public String getName() { return name; }


    /**
     * Getter for the country attribute
     * @return the country in which this city is located
     */
    public Country getCountry() { return country; }


    /**
     * Getter for the airports attribute
     * @return the list of airports in this city
     */
    public ArrayList<Airport> getAirports() { return airports; }


    /**
     * Add an airport to the airport array
     * @param a: the airport to add
     */
    public void addAirport(Airport a) {
        if (a != null)
            getAirports().add(a);
    }


    // Override ----------------------------------------------

    /**
     * toString override for a City object
     * @return a string displaying the name of the city and its country
     */
    @Override
    public String toString() {
        return "THIS CITY IS " + getName() + " (" + getCountry().getName() + ")";
    }


    /**
     * Override of the equals method
     * It only compares the name of the specified country on purpose
     * @param o: the object to compare
     * @return true if these objects are considered equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return getName().equals(city.getName()) &&
                getCountry().equals(city.getCountry());
    }
}
