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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public final class FlightRequest {

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
     */
    public static FlightList getFlightsAirportDest(String airport_name) throws Exception {
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
     */
    public static FlightList getFlightsAirportTo(String airport_name) throws Exception {
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


    /*public static FlightList getFlightsBetweenAirports(String airport1_name, String airport2_name) {

    }*/
}
