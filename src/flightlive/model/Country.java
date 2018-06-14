package flightlive.model;

import java.util.ArrayList;

/**
 * This class represents a country. It has access to the cities belonging to it.
 */
public final class Country {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    private String name;
    private ArrayList<City> cities;


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- CONSTRUCTOR ---------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /**
     * Constructor for a Country object
     * @param n: name of the country
     */
    public Country(String n) {
        name = n; cities = new ArrayList<>();
    }


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */


    /**
     * Getter for the name attribute
     * @return the name of this country
     */
    public String getName() { return name; }


    /**
     * Getter for the cities attribute
     * @return the list of cities in this country
     */
    public ArrayList<City> getCities() { return cities; }


    /**
     * Looks for the city with the given name and returns its code if found
     * @param cityName: the name of the city to look for
     * @return City object if found, null otherwise
     */
    public City getCityByName(String cityName) {
        for (City c : cities) {
            if (c.getName().equals(cityName))
                return c;
        }
        return null;
    }


    /**
     * Add a city to the cities array
     * @param c: the city to add
     */
    public void addCity(City c) {
        if (c != null)
            getCities().add(c);
    }


    // Override ----------------------------------------------

    /**
     * toString override for a Country object
     * @return a string displaying the name of the country
     */
    @Override
    public String toString() {
        return "COUNTRY: " + getName();
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
        Country country = (Country) o;
        return getName().equals(country.getName());
    }
}
