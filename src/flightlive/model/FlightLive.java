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
import java.util.Collections;
import java.util.Comparator;

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
        if (countries != null) {
            Collections.sort(countries, new Comparator<Country>() {
                @Override
                public int compare(Country c1, Country c2) {
                    return c1.getName().compareTo(c2.getName());
                }
            });
        }
    }


    /* /////////////////////////////////////////////////////////////////////////////// */
    /* ---------------------------------- METHODS ------------------------------------ */
    /* /////////////////////////////////////////////////////////////////////////////// */

    /**
     * Getter for the countries attribute
     * @return this object's countries attribute
     */
    public ArrayList<Country> getCountries() { return countries; }


    /**
     * Looks for a country with the given name
     * @param countryName the name of the country
     * @return the corresponding country object, null otherwise
     */
    public Country getCountryByName(String countryName) {
        for (Country c : countries) {
            if (c.getName().equals(countryName))
                return c;
        }
        return null;
    }


    /**
     * Looks for the airport with the given name and returns its icao code if found
     * @param n: the name of the airport to look for
     * @return its icao code if found, null otherwise
     */
    public String getAirportIcao(String n) {
        for (Country c : countries) {
            for (City ci : c.getCities()) {
                for (Airport a : ci.getAirports()) {
                    if (a.getName().equals(n))
                        return a.getIcao();
                }
            }
        }
        System.err.println("Error: airport not found");
        return null;
    }


    /**
     * Looks for the city with the given name and returns its code if found
     * @param n: the name of the city to look for
     * @return the City object if found, null otherwise
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
     * @param airport_icao icao of the airport
     * @return Return a FlightList with the flights concerned
     * @throws Exception
     */
    public FlightList getFlightsAirport(String airport_icao) throws Exception {
        String filter = "fAirQ=" + airport_icao; // Creating the filter
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
        if (airport_icao == null)
            return null;
        // Making the request
        FlightList flights = getFlightsAirport(airport_icao);
        if (flights == null)
            return null;

        // Parsing data
        ArrayList<Flight> res_fl = new ArrayList<>();
        for (int i = 0 ; i < flights.getAcList().length ; i++) {
            // If this flight goes to the given destination
            if (flights.getAcList()[i].To.split(" ")[0].equals(airport_icao))
                res_fl.add(flights.getAcList()[i]);
        }
        if (res_fl.size() == 0)
            return null;

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
        if (airport_icao == null)
            return null;
        // Making the request
        FlightList flights = getFlightsAirport(airport_icao);
        if (flights == null)
            return null;

        // Parsing data
        ArrayList<Flight> res_fl = new ArrayList<>();
        for (int i = 0 ; i < flights.getAcList().length ; i++) {
            // If this flight comes from the given destination
            if (flights.getAcList()[i].From.split(" ")[0].equals(airport_icao))
                res_fl.add(flights.getAcList()[i]);
        }
        if (res_fl.size() == 0)
            return null;

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
        String arr_airport_icao = getAirportIcao(airport_arr);

        if (from_airport == null)
            return null;
        if (from_airport.getAcList().length == 0)
            return null;

        FlightList res = new FlightList();
        ArrayList<Flight> res_fl = new ArrayList<>();

        for (Flight f : from_airport.getAcList()) {
            if (f.To.length() >= 4) {
                // If this flight goes to the destination airport
                if (f.To.substring(0,4).equals(arr_airport_icao))
                    res_fl.add(f);
            }
        }

        res.setAcList(res_fl.stream().toArray(Flight[]::new));
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
        if (city1 == null || city2 == null)
            return null;

        // Getting the list of airports' icaos in this city
        ArrayList<String> dest_icaos = new ArrayList<>();
        for (Airport a : city2.getAirports())
            dest_icaos.add(a.getIcao());

        // Search flight between these 2 cities
        ArrayList<Flight> res_fl = new ArrayList<>();
        FlightList tmp;

        for (Airport a : city1.getAirports()) {
            tmp = getFlightsAirportFrom(a.getName());
            if (tmp != null) {
                for (Flight f : tmp.getAcList()) {  // For each flight coming from this airport
                    if (f.To.length() >= 4) {
                        // If this flight goes to one of the airports in the destination city
                        if (dest_icaos.contains(f.To.substring(0,4)))
                            res_fl.add(f);
                    }
                }
            }
        }

        // Setting up our result
        res.setAcList(res_fl.stream().toArray(Flight[]::new));
        return res;
    }


    /**
     * Get flights coming from a given city and going to the specified airport
     * @param cityName the departure city
     * @param airportName the arrival airport
     * @return FlightList object which contains corresponding flights
     * @throws Exception
     */
    public FlightList getFlightsFromCityToAirport(String cityName, String airportName) throws Exception {
        FlightList flightsFromCity = getFlightsCityFrom(cityName);
        String airportIcao = getAirportIcao(airportName);
        if (flightsFromCity == null)
            return null;

        if (flightsFromCity.getAcList().length == 0)
            return null;

        FlightList res = new FlightList();
        ArrayList<Flight> res_fl = new ArrayList<>();

        for (Flight f : flightsFromCity.getAcList()) {
            if (f.To.length() >= 4) {
                if (f.To.substring(0,4).equals(airportIcao))
                    res_fl.add(f);
            }
        }

        // Setting up our result
        res.setAcList(res_fl.stream().toArray(Flight[]::new));
        return res;
    }


    /**
     * Get flights coming from a given airport and going to the specified city
     * @param airportName the departure airport
     * @param cityName the arrival city
     * @return FlightList object which contains corresponding flights
     * @throws Exception
     */
    public FlightList getFlightsFromAirportToCity(String airportName, String cityName) throws Exception {
        FlightList flightsToCity = getFlightsCityTo(cityName);
        String airportIcao = getAirportIcao(airportName);
        if (flightsToCity == null)
            return null;

        if (flightsToCity.getAcList().length == 0)
            return null;

        FlightList res = new FlightList();
        ArrayList<Flight> res_fl = new ArrayList<>();

        for (Flight f : flightsToCity.getAcList()) {
            if (f.From.length() >= 4) {
                if (f.From.substring(0,4).equals(airportIcao))
                    res_fl.add(f);
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
