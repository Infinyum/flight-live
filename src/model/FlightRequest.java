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
     * Give the flight from and to the airport given in argmuments
     * @param airport_icao  ICAO of the airport
     * @return Return a FlightList with the flights concerned
     */
    public static FlightList getFlightsAirport(String airport_icao) throws Exception {
        String filters = "fAirQ=";
        FlightList flights;

        // Create filters
        filters += airport_icao;

        // Do request and test results
        flights = FlightRequest.asynch_request(filters);
        for(Flight f : flights.getAcList()) {
            if (!airport_icao.equals(f.From.split(" ")[0]) && !airport_icao.equals(f.To.split(" ")[0])) {
                System.err.println("Error : Request gived bad results");
                return null;
            }
        }

        return flights;
    }

    /**
     * Give flights with destination the airport given in argmuments
     * @param airport_name  Name of the destination airport
     * @return Return a FlightList with the flights concerned
     */
    public static FlightList getFlightsAirportDest(String airport_name) throws Exception {
        FlightList flights, res = new FlightList();
        Flight[] res_acList;
        int nb_dest = 0, i_res = 0;
        String airport_icao;

        // Get Airport ICAO
        airport_icao = FlightLive.getAirportIcao(airport_name);
        if(airport_icao == null) {
            System.err.println("Error : Airport ICAO not found");
            return null;
        }

        // Do the request
        flights = FlightRequest.getFlightsAirport(airport_icao);
        if(flights == null)
            return null;

        // Parse datas
        for(int i = 0; i < flights.getAcList().length; i++) {
            if(flights.getAcList()[i].To.split(" ")[0].equals(airport_icao))
                nb_dest++;
        }
        if(nb_dest == 0) {
            System.err.println("No flight to this destination");
            return null;
        }

        // Create acList for final result
        res_acList = new Flight[nb_dest];

        // Add flights to the result acList
        for(int i = 0; i < flights.getAcList().length; i++) {
            if(flights.getAcList()[i].To.split(" ")[0].equals(airport_icao))
                res_acList[i_res] = flights.getAcList()[i];
        }

        // Setup res
        res.setAcList(res_acList);
        res.setLastDv(flights.getLastDv());

        return res;
    }
}
