package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

final class CSVParser {

    /**
     * Parses the csv file to create all the Airport, City and Country objects
     * then returns the list of Country objects
     * @return the list of countries
     */
    static ArrayList<Country> parseCSV() {

        ArrayList<City> cities = new ArrayList<>();
        ArrayList<Country> countries = new ArrayList<>();

        try {
            FileReader file = new FileReader("deps/airports.csv");
            BufferedReader bufRead = new BufferedReader(file);

            String line = bufRead.readLine();
            while (line != null) {
                String[] array = line.split(",");

                // Retrieving the info in the csv
                String name = array[0];
                String cityName = array[1];
                String countryName = array[2];
                String icao = array[3];
                double lat = Double.parseDouble(array[4]);
                double lon = Double.parseDouble(array[5]);

                // Looking for an existing country in the array
                Country country = getCountryByName(countries, countryName);
                // ...if not found, then create a new Country object and add it to the array
                if (country == null) {
                    country = new Country(countryName);
                    countries.add(country);
                }

                // Looking for an existing city in the array
                City city = getCityByNameAndCountry(cities, cityName, country);
                // ...if not found, then create a new City object and add it to the array
                if (city == null) {
                    city = new City(cityName, country);
                    cities.add(city);
                }

                // Creating a new Airport object
                Airport airport = new Airport(name, city, icao, lat, lon);
                // Adding the new airport to its city
                city.addAirport(airport);

                // It the city doesn't appear in the list of cities of this country, we add it
                if (getCityByNameAndCountry(country.getCities(), cityName, country) == null)
                    country.addCity(city);

                line = bufRead.readLine();
            }

            bufRead.close();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return countries;
    }


    /**
     * Look for a country with a specified name in a list
     * @param countries: the list of countries
     * @param countryName: the name of the country we are looking for
     * @return the country object if it was found, null otherwise
     */
    private static Country getCountryByName(ArrayList<Country> countries, String countryName) {
        for (Country c : countries) {
            if (c.getName().equals(countryName)) return c;
        }
        return null;
    }


    /**
     * Look for a city with a specified name and country in a list
     * @param cities: the list of cities
     * @param cityName: the name of the city we are looking for
     * @param country: the country in which the city is supposed to be
     * @return the city object if it was found, null otherwise
     */
    private static City getCityByNameAndCountry(ArrayList<City> cities, String cityName, Country country) {
        for (City c : cities) {
            if (c.getName().equals(cityName) && c.getCountry().equals(country)) return c;
        }
        return null;
    }
}
