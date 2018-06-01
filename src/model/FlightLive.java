package model;

import java.util.ArrayList;

public final class FlightLive {
    private static ArrayList<Country> countries = null;


    public static void main(String[] args) {
        countries = CSVParser.parseCSV();

        Country country = countries.get(42);
        City city = country.getCities().get(0);
        for (Airport a : city.getAirports()) System.out.println(a.toStringShort());
    }
}
