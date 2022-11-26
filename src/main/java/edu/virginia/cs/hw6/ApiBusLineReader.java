package edu.virginia.cs.hw6;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class ApiBusLineReader implements BusLineReader {
    List<BusLine> busLinesList;
    //constructor
    public ApiBusLineReader() {
        busLinesList = new ArrayList<>();
    }
    ConfigSingleton config = ConfigSingleton.getInstance();
    public final String busLineApiURL = config.getBusLinesURL();
    public final String stopApiURL = config.getBusStopsURL();

    @Override
    public List<BusLine> getBusLines() {
        List<Stop> stopsList = new ArrayList<>();
//        ApiBusLineReader uses both:
//        Most of the contents of the BusLine object from the "lines" endpoint
//        The Route must be built using the "routes" value in the "stops" endpoint
//        The routes list the "stops" in-order of how they are visited on the BusLine
//        Be aware that your code will be tested on other data than the endpoints above.
//        However, all JSON examples we use in testing will be formatted identically.
//        A note: If there are stop-ids in the JSON file under "routes" that do not have a matching stop,
//        simply "skip" that Stop, don't create a Stop object for it, and don't add it to Routes
        try {
            String jsonText = getJSONText();
            JSONObject o = new JSONObject(jsonText);
            JSONArray lines = o.getJSONArray("routes");
            //store id, isActive,longName, shortName in BusLine
            for (Object m : lines) {
                if (m instanceof JSONObject) {
                    JSONObject line = (JSONObject) m;
                    int id = line.getInt("id");
                    boolean isActive = line.getBoolean("is_active");
                    String longName = line.getString("long_name");
                    String shortName = line.getString("short_name");
                    JSONArray a = getRoutes();
                    for (Object n : a) {
                        if (n instanceof JSONObject) {
                            JSONObject route = (JSONObject) n;
                            int routeId = route.getInt("id");
                            if (routeId == id) {
                                JSONArray stops = route.getJSONArray("stops");
                                //get the JSONArray of stops from the ApiStopReader
                                ApiStopReader stopReader = new ApiStopReader();
                                JSONArray stopsArray = stopReader.getStopsArrayFromstopsEndpoint();
                                //For each id in the stops JSONArray, find the corresponding stop in the stopsArray
                                //If it exists, add it to the Route
                                //If it does not exist, skip it
                                stopsList = checkID(stops, stopsArray);
                               //Create a Route object with the list of Stops
                                Route r = new Route(stopsList);
                                //Create a BusLine object with the id, isActive, longName, shortName, and Route
                                BusLine b = new BusLine(id, isActive, longName, shortName, r);
                                busLinesList.add(b);

                            }
                        }
                    }

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return busLinesList;
    }

    private static List<Stop> checkID(JSONArray stops, JSONArray stopsArray) {
        List<Stop> stopList = new ArrayList<>();
        for (Object o1 : stops) {
            if (o1 instanceof Integer) {
                int stopId = (int) o1;
                for (Object o2 : stopsArray) {
                    if (o2 instanceof JSONObject) {
                        JSONObject stop = (JSONObject) o2;
                        int stopId2 = stop.getInt("id");
                        if (stopId == stopId2) {
                            String stopName = stop.getString("name");
                            JSONArray position = stop.getJSONArray("position");
                            double longitude = position.getDouble(0);
                            double latitude = position.getDouble(1);
                            Stop s = new Stop(stopId, stopName, longitude, latitude);
                            stopList.add(s);

                        }
                    }
                }
            }
        }
        return stopList;
    }

    //Go to the stops endpoint and get the "routes" array
    private JSONArray getRoutes() {
        try {
            String jsonText = getJSONText2();
            JSONObject o = new JSONObject(jsonText);
            JSONArray routes = o.getJSONArray("routes");
            return routes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getJSONText() throws IOException {
        InputStreamReader apiReader = getAPIReader();
        return getTextFromAPIReader(apiReader);
    }

    private String getJSONText2() throws IOException {
        InputStreamReader apiReader = getAPIReader2();
        return getTextFromAPIReader(apiReader);
    }

    private InputStreamReader getAPIReader2() throws IOException {
        URL stopURL = new URL(stopApiURL);
        InputStream apiStream = stopURL.openStream();
        return new InputStreamReader(apiStream, StandardCharsets.UTF_8);
    }

    private InputStreamReader getAPIReader() throws IOException {
        URL stopURL = new URL(busLineApiURL);
        InputStream apiStream = stopURL.openStream();
        return new InputStreamReader(apiStream, StandardCharsets.UTF_8);
    }

    private String getTextFromAPIReader(InputStreamReader apiReader) {
        BufferedReader bufferedReader = new BufferedReader(apiReader);
        return bufferedReader.lines().collect(Collectors.joining());
    }

    //write a main method to test your code
    public static void main(String[] args) {
        ApiBusLineReader reader = new ApiBusLineReader();
        List<BusLine> busLines = reader.getBusLines();
        for (BusLine b : busLines) {
            System.out.println(b);
        }
    }

}
