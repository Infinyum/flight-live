package model;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.internal.ArrayIterator;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

public final class FlightRequest {

    /**
     * Execute a http request to get informations about flights
     * @param filters   Filters to apply to the request
     * @return FlightList which contains flight results
     * @throws Exception
     */
    public static FlightList asynch_request(String filters) throws Exception {
        FlightList flights;

        // Setup http client
        DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
                .setConnectTimeout(500)
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")
                .setKeepAlive(false);
        AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder);

        // Create request GET
        BoundRequestBuilder getRequest = client.prepareGet("https://public-api.adsbexchange.com/VirtualRadar/AircraftList.json?" + filters);

        // Execute et get result
        flights = getRequest.execute(new AsyncCompletionHandler<FlightList>() {
            @Override
            public FlightList onCompleted(Response response) throws Exception {
                FlightList flights;

                ObjectMapper mapper = new ObjectMapper();
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); //Ignorer les champs inutiles
                flights = mapper.readValue(response.getResponseBody(), FlightList.class); //Créer l'objet de plus haut niveau dans le dictionnaire json

                return flights;
            }
        }).get();

        return flights;
    }


    /**
     * Returns the list of flights going to and coming from the airport whose icao is
     * passed as an argument
     * @param airport_icao ICAO of the airport
     * @return Return a FlightList with the flights concerned
     * @throws Exception
     */
    public static FlightList getFlightsAirport(String airport_icao) throws Exception {
        // Creating the filter
        String filter = "fAirQ=" + airport_icao;
        // Executing and returning the request
        return FlightRequest.asynch_request(filter);
    }


    /**
     * Looks for flights going to the airport given in arguments
     * @param airport_name  Name of the destination airport
     * @return Return a FlightList with the flights concerned
     * @throws Exception
     */
    public static FlightList getFlightsAirportTo(String airport_name) throws Exception {
        // Get Airport ICAO
        String airport_icao = FlightLive.getAirportIcao(airport_name);
        if (airport_icao == null) {
            System.err.println("ERROR:" + airport_name + "'s ICAO not found");
            return null;
        }

        // Do the request
        FlightList flights = FlightRequest.getFlightsAirport(airport_icao);

        // Parse data
        ArrayList<Flight> res_fl = new ArrayList<>();
        for (int i = 0 ; i < flights.getAcList().length ; i++) {
            if(flights.getAcList()[i].To.split(" ")[0].equals(airport_icao))
                res_fl.add(flights.getAcList()[i]);
        }
        if (res_fl.size() == 0) {
            System.err.println("No flight to this destination");
            return null;
        }

        // Setting up res
        FlightList res = new FlightList();
        res.setAcList(res_fl.stream().toArray(Flight[]::new));
        res.setLastDv(flights.getLastDv());

        return res;
    }


    /**
     * Looks for flights coming from the airport given in arguments
     * @param airport_name  Name of the departure airport
     * @return Return a FlightList with the flights concerned
     * @throws Exception
     */
    public static FlightList getFlightsAirportFrom(String airport_name) throws Exception {
        // Get Airport ICAO
        String airport_icao = FlightLive.getAirportIcao(airport_name);
        if (airport_icao == null) {
            System.err.println("ERROR: Airport ICAO not found");
            return null;
        }

        // Do the request
        FlightList flights = FlightRequest.getFlightsAirport(airport_icao);

        // Parse data
        ArrayList<Flight> res_fl = new ArrayList<>();
        for (int i = 0 ; i < flights.getAcList().length ; i++) {
            if(flights.getAcList()[i].From.split(" ")[0].equals(airport_icao))
                res_fl.add(flights.getAcList()[i]);
        }
        if (res_fl.size() == 0) {
            System.err.println("No flight from this destination");
            return null;
        }

        // Setting up res
        FlightList res = new FlightList();
        res.setAcList(res_fl.stream().toArray(Flight[]::new));
        res.setLastDv(flights.getLastDv());

        return res;
    }


    /**
     * Looks for all the flights going from airport_dep to airport_arr
     * @param airport_dep: departure airport
     * @param airport_arr: arrival airport
     * @return the FlightList object containing all the flights corresponding
     * @throws Exception
     */
    public static FlightList getFlightsBetweenAirports(String airport_dep, String airport_arr) throws Exception {
        FlightList from_airport = getFlightsAirportFrom(airport_dep);
        FlightList to_airport = getFlightsAirportTo(airport_arr);

        if (from_airport == null || to_airport == null)
            return null;

        if (from_airport.getAcList().length == 0 || to_airport.getAcList().length == 0)
            return null;

        ArrayList<Flight> dep_list = new ArrayList<>(Arrays.asList(from_airport.getAcList()));
        ArrayList<Flight> arr_list = new ArrayList<>(Arrays.asList(to_airport.getAcList()));

        ArrayList<Flight> res_array = new ArrayList<>();

        for (Flight f : dep_list) {
            if (arr_list.contains(f)) res_array.add(f);
        }

        // Setting up res
        FlightList res = new FlightList();
        res.setAcList(res_array.stream().toArray(Flight[]::new));
        res.setLastDv(from_airport.getLastDv());

        return res;
    }

    /**
     * Research flights with destination the city given in arguments
     * @param city_name Name of the city
     * @return FlightList which contains result flights
     */
    public static FlightList getFlightsCityTo(String city_name) throws Exception {
        FlightList res = new FlightList();

        // Research of the city object
        City city = FlightLive.getCity(city_name);
        if(city == null)
            return null;

        // Research of flights for each airport of the city
        ArrayList<Flight> res_fl = new ArrayList<>();
        FlightList tmp;
        for(Airport a : city.getAirports()) {
            tmp = FlightRequest.getFlightsAirportTo(a.getName());
            if(tmp != null)
                res_fl.addAll(Arrays.asList(tmp.getAcList()));
        }

        // Add results to res FlightList
        res.setAcList(res_fl.stream().toArray(Flight[]::new));

        return res;
    }

    /**
     * Research flights from the city given in arguments
     * @param city_name Name of the city
     * @return FlightList which contains result flights
     */
    public static FlightList getFlightsCityFrom(String city_name) throws Exception {
        FlightList res = new FlightList();

        // Research of the city object
        City city = FlightLive.getCity(city_name);
        if(city == null)
            return null;

        // Research of flights for each airport of the city
        ArrayList<Flight> res_fl = new ArrayList<>();
        FlightList tmp;
        for(Airport a : city.getAirports()) {
            tmp = FlightRequest.getFlightsAirportFrom(a.getName());
            if(tmp != null)
                res_fl.addAll(Arrays.asList(tmp.getAcList()));
        }

        // Add results to res FlightList
        res.setAcList(res_fl.stream().toArray(Flight[]::new));

        return res;
    }

    /**
     * Get flights from city1 to the city2 airports
     * @param city1_name    Departure city
     * @param city2_name    Destination city
     * @return FlightList which contains flights between these 2 cities
     * @throws Exception
     */
    public static FlightList getFlightsCities(String city1_name, String city2_name) throws Exception {
        FlightList res = new FlightList();

        // Research of the cities
        City city1 = FlightLive.getCity(city1_name);
        City city2 = FlightLive.getCity(city2_name);
        if(city1 == null || city2 == null)
            return null;

        // Search flight between these 2 cities
        ArrayList<Flight> res_fl = new ArrayList<>();
        FlightList tmp;
        for(Airport a1 : city1.getAirports()) {
            System.err.println("From : "+a1.getName());
            for(Airport a2 : city2.getAirports()) {
                System.err.println("To : "+a2.getName());
                tmp = FlightRequest.getFlightsBetweenAirports(a1.getName(), a2.getName());
                if(tmp != null)
                    res_fl.addAll(Arrays.asList(tmp.getAcList()));
            }
        }

        // Setup our result
        res.setAcList(res_fl.stream().toArray(Flight[]::new));

        return res;
    }

    /**
     * Displays the position history of the given flight
     * @param f: the flight to track
     * @throws Exception
     */
    public static void displayFlightPositionHistory(Flight f) throws Exception {
        String icao = f.getIcao();
        String filter = "fIcoQ=" + icao + "&trFmt=f";
        FlightList flights = asynch_request(filter);
        //ArrayList<Double> position_history = new ArrayList<>(Arrays.asList(flights.getAcList()[0].getCot()));
        if (flights != null && flights.getAcList() != null && flights.getAcList()[0] != null) {
            double[] position_history = flights.getAcList()[0].getCot();
            boolean lat = false;
            for (int i = 0 ; i < position_history.length ; i++) {
                if ((i+1) % 3 == 0) System.out.println();
                else {
                    if (!lat) {
                        System.out.print("Lat: " + position_history[i]);
                        lat = true;
                    } else {
                        System.out.println(", Lon: " + position_history[i] + "\n");
                        lat = false;
                    }
                }
            }
        }
    }

    public static FlightList getFlightsAroundPosition(String lat, String lon, String radius) throws Exception {
        String filter = "lat=" + lat + "&lng=" + lon + "&fDstL=0&fDstU=" + radius;
        FlightList flights = asynch_request(filter);

        if (flights == null || flights.getAcList().length == 0) {
            System.out.println("\nNULL\n\n");
            return null;
        }
        return flights;
    }
}
