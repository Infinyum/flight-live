package model;

import java.util.ArrayList;
import java.util.Scanner;

public final class FlightLive {
    private static ArrayList<Country> countries = null;

    public static void main(String[] args) throws Exception {
        countries = CSVParser.parseCSV();

        Scanner input = new Scanner(System.in);

        String airportName;
        //System.out.println("Please enter an airport name: ");
        //airportName = input.nextLine();

        FlightList fl = FlightRequest.getFlightsCities("New York", "Paris");
        System.out.println(fl);
    }

    /**
     * Looks for the airport with the given name and returns its code if found
     * @param n: the name of the airport to look for
     * @return its icao code if found, null otherwise
     */
    public static String getAirportIcao(String n) {
        for (Country c : countries) {
            for (City ci : c.getCities()) {
                for (Airport a : ci.getAirports()) {
                    if (a.getName().equals(n)) return a.getIcao();
                }
            }
        }

        System.err.println("Error : Airport not found");

        return null;
    }

    /**
     * Looks for the city with the given name and returns its code if found
     * @param n: the name of the city to look for
     * @return its the City object if found, null otherwise
     */
    public static City getCity(String n) {
        for (Country c : countries) {
            for (City ci : c.getCities()) {
                if (ci.getName().equals(n)) return ci;
            }
        }

        System.err.println("Error : City not found");

        return null;
    }
}
