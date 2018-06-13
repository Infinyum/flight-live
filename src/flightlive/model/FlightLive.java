package flightlive.model;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The main class of the modem. It contains the list of countries and therefore
 * all the objects of the program. Therefore, this class is supposed to perform some
 * analyses with the data. It also sends the JSON requests to the database and builds
 * relevant objects structures.
 */
public final class FlightLive {
    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- ATTRIBUTES ----------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    private static ArrayList<Country> countries = null;


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* -------------------------------- CONSTRUCTOR ---------------------------------- */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /**
     * Constructor for the FlightLive class. Parses the CSV file to build the objects.
     */
    public FlightLive() {
        countries = CSVParser.parseCSV();
    }


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /**
     * Looks for the airport with the given name and returns its code if found
     * @param n: the name of the airport to look for
     * @return its icao code if found, null otherwise
     */
    public String getAirportIcao(String n) {
        for (Country c : countries) {
            for (City ci : c.getCities()) {
                for (Airport a : ci.getAirports()) {
                    if (a.getName().equals(n)) return a.getIcao();
                }
            }
        }
        System.err.println("Error: airport not found");
        return null;
    }


    /**
     * Looks for the city with the given name and returns its code if found
     * @param n: the name of the city to look for
     * @return its the City object if found, null otherwise
     */
    public City getCity(String n) {
        for (Country c : countries) {
            for (City ci : c.getCities()) {
                if (ci.getName().equals(n)) return ci;
            }
        }
        System.err.println("Error: city not found");
        return null;
    }


    /**
     * Executes an http request to get informations about flights
     * @param filters Filters to apply to the request
     * @return FlightList which contains flight results
     * @throws Exception
     */
    public FlightList asynch_request(String filters) throws Exception {
        FlightList flights;

        // Setup http client
        DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config()
                .setConnectTimeout(500)
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")
                .setKeepAlive(false);
        AsyncHttpClient client = Dsl.asyncHttpClient(clientBuilder);

        // Create request GET
        BoundRequestBuilder getRequest = client.prepareGet("https://public-api.adsbexchange.com/VirtualRadar/AircraftList.json?" + filters);

        // Executes and retrieves the result
        flights = getRequest.execute(new AsyncCompletionHandler<FlightList>() {
            @Override
            public FlightList onCompleted(Response response) throws Exception {
                FlightList flights;

                ObjectMapper mapper = new ObjectMapper();
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // Ignore useless attributes
                // Creates the highest level object in the JSON dictionary
                flights = mapper.readValue(response.getResponseBody(), FlightList.class);

                return flights;
            }
        }).get();

        return flights;
    }


    /**
     * Returns the list of flights going to and coming from the airport whose icao is
     * passed as an argument
     * @param airport_name name of the airport
     * @return Return a FlightList with the flights concerned
     * @throws Exception
     */
    public FlightList getFlightsAirport(String airport_name) throws Exception {
        String icao = getAirportIcao(airport_name);
        if (icao == null)
            return null;
        String filter = "fAirQ=" + icao;        // Creating the filter
        return asynch_request(filter);    // Executing and returning the request
    }


    /**
     * Looks for flights going to the airport given in arguments
     * @param airport_name  Name of the destination airport
     * @return Return a FlightList with the flights concerned
     * @throws Exception
     */
    public FlightList getFlightsAirportTo(String airport_name) throws Exception {
        String airport_icao = getAirportIcao(airport_name); // Getting the airport's ICAO
        if (airport_icao == null) {
            System.err.println("ERROR:" + airport_name + "'s ICAO not found");
            return null;
        }
        // Making the request
        FlightList flights = getFlightsAirport(airport_icao);

        // Parsing data
        ArrayList<Flight> res_fl = new ArrayList<>();
        for (int i = 0 ; i < flights.getAcList().length ; i++) {
            // If this flight goes to the given destination
            if (flights.getAcList()[i].To.split(" ")[0].equals(airport_icao))
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
    public FlightList getFlightsAirportFrom(String airport_name) throws Exception {
        String airport_icao = getAirportIcao(airport_name); // Getting the airport's ICAO
        if (airport_icao == null) {
            System.err.println("ERROR:" + airport_name + "'s ICAO not found");
            return null;
        }
        // Making the request
        FlightList flights = getFlightsAirport(airport_icao);

        // Parsing data
        ArrayList<Flight> res_fl = new ArrayList<>();
        for (int i = 0 ; i < flights.getAcList().length ; i++) {
            // If this flight comes from the given destination
            if (flights.getAcList()[i].From.split(" ")[0].equals(airport_icao))
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
    public FlightList getFlightsBetweenAirports(String airport_dep, String airport_arr) throws Exception {
        FlightList from_airport = getFlightsAirportFrom(airport_dep);
        FlightList to_airport = getFlightsAirportTo(airport_arr);
        // No object was created
        if (from_airport == null || to_airport == null)
            return null;
        // No flight was found
        if (from_airport.getAcList().length == 0 || to_airport.getAcList().length == 0)
            return null;

        ArrayList<Flight> departures_list = new ArrayList<>(Arrays.asList(from_airport.getAcList()));
        ArrayList<Flight> arrivals_list = new ArrayList<>(Arrays.asList(to_airport.getAcList()));
        ArrayList<Flight> resulting_array = new ArrayList<>();

        // Adding the relevant flights to the resulting array
        for (Flight f : departures_list) {
            if (arrivals_list.contains(f)) resulting_array.add(f);
        }

        // Setting up res
        FlightList res = new FlightList();
        res.setAcList(resulting_array.stream().toArray(Flight[]::new));
        res.setLastDv(from_airport.getLastDv());
        return res;
    }


    /**
     * Looks for flights going to the city given in arguments
     * @param city_name Name of the city
     * @return FlightList which contains result flights
     */
    public FlightList getFlightsCityTo(String city_name) throws Exception {
        FlightList res = new FlightList();

        // Research of the city object
        City city = getCity(city_name);
        if (city == null)
            return null;

        // Research of flights for each airport of the city
        ArrayList<Flight> res_fl = new ArrayList<>();
        FlightList tmp;
        for (Airport a : city.getAirports()) {
            tmp = getFlightsAirportTo(a.getName());
            if (tmp != null)
                res_fl.addAll(Arrays.asList(tmp.getAcList()));
        }

        // Adding results to res FlightList
        res.setAcList(res_fl.stream().toArray(Flight[]::new));
        return res;
    }


    /**
     * Research flights coming from the city given in arguments
     * @param city_name Name of the city
     * @return FlightList which contains result flights
     */
    public FlightList getFlightsCityFrom(String city_name) throws Exception {
        FlightList res = new FlightList();

        // Research of the city object
        City city = getCity(city_name);
        if(city == null)
            return null;

        // Research of flights for each airport of the city
        ArrayList<Flight> res_fl = new ArrayList<>();
        FlightList tmp;
        for (Airport a : city.getAirports()) {
            tmp = getFlightsAirportFrom(a.getName());
            if (tmp != null)
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
    public FlightList getFlightsCities(String city1_name, String city2_name) throws Exception {
        FlightList res = new FlightList();

        // Research of the cities
        City city1 = getCity(city1_name);
        City city2 = getCity(city2_name);
        if(city1 == null || city2 == null)
            return null;

        // Search flight between these 2 cities
        ArrayList<Flight> res_fl = new ArrayList<>();
        FlightList tmp;

        for (Airport a1 : city1.getAirports()) {
            System.err.println("From: " + a1.getName());

            for (Airport a2 : city2.getAirports()) {
                System.err.println("To: " + a2.getName());
                tmp = getFlightsBetweenAirports(a1.getName(), a2.getName());
                if (tmp != null)
                    res_fl.addAll(Arrays.asList(tmp.getAcList()));
            }
        }

        // Setting up our result
        res.setAcList(res_fl.stream().toArray(Flight[]::new));
        return res;
    }

    /**
     * Displays the position history of the given flight
     * @param f the flight to track
     * @throws Exception
     */
    public void displayFlightPositionHistory(Flight f) throws Exception {
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


    /**
     * Get all the flights within a given radius around a given position
     * @param lat a latitude
     * @param lon a longitude
     * @param radius a radius
     * @return the list of flights matching the arguments
     * @throws Exception
     */
    public FlightList getFlightsAroundPosition(String lat, String lon, String radius) throws Exception {
        String filter = "lat=" + lat + "&lng=" + lon + "&fDstL=0&fDstU=" + radius;
        FlightList flights = asynch_request(filter);

        if (flights == null || flights.getAcList().length == 0) // No flights found
            return null;

        return flights;
    }
}
