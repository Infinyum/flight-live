package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

final class CSVParser {

    /**
     * Parses the csv file to create Airport objects and add them to airports
     * @param airports: an ArrayList of Airport objects
     * @return airports with the list of Airport objects
     */
    static ArrayList<Airport> parseCSV(ArrayList<Airport> airports) {

        Airport currentAirport;

        try {
            FileReader file = new FileReader("deps/airports.csv");
            BufferedReader bufRead = new BufferedReader(file);

            String line = bufRead.readLine();
            while (line != null) {
                String[] array = line.split(",");

                String name = array[0];
                String city = array[1];
                String country = array[2];
                String icao = array[3];
                double lat = Double.parseDouble(array[4]);
                double lon = Double.parseDouble(array[5]);

                currentAirport = new Airport(name, city, country, icao, lat, lon);
                airports.add(currentAirport);

                line = bufRead.readLine();
            }

            bufRead.close();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return airports;
    }
}
