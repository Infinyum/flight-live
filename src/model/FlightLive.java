package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FlightLive {
    public static void main(String[] args) {
        try {
            FileReader file = new FileReader("deps/airports.csv");
            BufferedReader bufRead = new BufferedReader(file);

            String line = bufRead.readLine();
            while (line != null) {
                String[] array = line.split(",");

                System.out.println(array[0]);

                line = bufRead.readLine();
            }

            bufRead.close();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
