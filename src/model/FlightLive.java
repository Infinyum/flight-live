package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class FlightLive {

    //private ArrayList<Airport> airports;

    public static void main(String[] args) {

        ArrayList<Airport> airports = new ArrayList<>();
        Airport currentAirport = null;

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
    }
}
