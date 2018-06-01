package model;

import java.util.ArrayList;

public final class FlightLive {
    private static ArrayList<Country> countries = null;

    public static void main(String[] args) {
        countries = CSVParser.parseCSV();
        // TOTAL: 7184 airports

        for (Country c : countries) {
            if (c.getName().equals("Haiti")) {
                System.out.println(c.getCities().size());
                for (City ci : c.getCities()) System.out.println(ci);
            }
        }
    }
}
