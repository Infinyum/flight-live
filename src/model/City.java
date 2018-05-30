package model;

import java.util.ArrayList;

public final class City {
    private String name;
    private ArrayList<Airport> airports;

    /**
     * Constructor for a City object
     * @param n: name of the city
     * @param a: array of the airports in the city
     */
    public City(String n, ArrayList<Airport> a) {
        name = n; airports = a;
    }
}
