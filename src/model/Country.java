package model;

import java.util.ArrayList;

public final class Country {
    private String name;
    private ArrayList<City> cities;

    /**
     * Constructor for a Country object
     * @param n: name of the country
     * @param c: array of cities in this country
     */
    public Country(String n, ArrayList<City> c) {
        name = n; cities = c;
    }


    /**
     * Getter for the name attribute
     * @return the name of this country
     */
    public String getName() { return name; }
}
