package model;

import java.util.ArrayList;

public final class City {
    private String name;
    private Country country;
    private ArrayList<Airport> airports;

    /**
     * Constructor for a City object
     * @param n: name of the city
     * @param c: the country in which this city is located
     */
    public City(String n, Country c) {
        name = n; country = c; airports = null;
    }


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


    /**
     * Add an airport to the airport array
     * @param a: the airport to add
     */
    public void addAirport(Airport a) {
        if (a != null)
            getAirports().add(a);
    }
}
