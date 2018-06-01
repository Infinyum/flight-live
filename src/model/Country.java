package model;

import java.util.ArrayList;

public final class Country {
    private String name;
    private ArrayList<City> cities;

    /**
     * Constructor for a Country object
     * @param n: name of the country
     */
    public Country(String n) {
        name = n; cities = new ArrayList<>();
    }


    /**
     * toString override for a Country object
     * @return a string displaying the name of the country
     */
    @Override
    public String toString() {
        return "COUNTRY: " + getName();
    }


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


    /**
     * Add a city to the cities array
     * @param c: the city to add
     */
    public void addCity(City c) {
        if (c != null)
            getCities().add(c);
    }
}
