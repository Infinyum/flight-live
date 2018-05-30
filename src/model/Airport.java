package model;

public final class Airport {
    private String name;
    private String city;
    private String country;
    private String icao;
    private double latitude;
    private double longitude;

    /**
     * Constructor for an Airport object
     * @param n: name of the airport
     * @param ci: city where the airport is located
     * @param co: country where the airport is located
     * @param i: ICAO code of the airport
     * @param lat: latitude of the airport
     * @param lon: longitude of the airport
     */
    public Airport(String n, String ci, String co, String i, double lat, double lon) {
        name = n; city = ci; country = co; icao = i; latitude = lat; longitude = lon;
    }


    /**
     * toString override
     * @return a decent way of displaying an Airport object's infos
     */
    @Override
    public String toString() {
        return "AIRPORT " + name + " in " + city + " (" + country + ")\nICAO: " + icao
                + "\nPOSITION: " + latitude + ", " + longitude;
    }
}
