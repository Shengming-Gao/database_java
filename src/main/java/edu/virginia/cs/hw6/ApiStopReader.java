package edu.virginia.cs.hw6;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;

public class ApiStopReader implements StopReader {
    private List<Stop> stopsList;

    //constructor
    public ApiStopReader() {
        stopsList = new ArrayList<>();
    }

    ConfigSingleton config = ConfigSingleton.getInstance();
    public final String stopApiURL = config.getBusStopsURL();

    @Override
    public List<Stop> getStops() {
        //returns a list of Stops from the "stops" endpoint
        //See Stop class for what data you need to extract
        //You will read these from the endpoints in the ConfigSingleton.
        //ApiStopReader uses the Stops endpoint using the "stops" value
        //A note: If there are stop-ids in the JSON file under "routes"
        //that do not have a matching stop, simply "skip" that Stop, don't create a
        //Stop object for it, and don't add it to Routes
        try {
            String jsonText = getJSONText();
            JSONObject o = new JSONObject(jsonText);
            JSONArray stops = o.getJSONArray("stops");
            for (Object m : stops) {
                if (m instanceof JSONObject) {
                    JSONObject stop = (JSONObject) m;
                    int stopId = stop.getInt("id");
                    String stopName = stop.getString("name");
                    //get longitude and latitude
                    //They are array elements in the "position" array
                    //You will need to get the first and second elements
                    //and convert them to doubles
                    JSONArray position = stop.getJSONArray("position");
                    double longitude = position.getDouble(0);
                    double latitude = position.getDouble(1);
                    Stop s = new Stop(stopId, stopName, longitude, latitude);
                    stopsList.add(s);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stopsList;
    }

    private String getJSONText() throws IOException {
        InputStreamReader apiReader = getAPIReader();
        return getTextFromAPIReader(apiReader);
    }

    private InputStreamReader getAPIReader() throws IOException {
        URL stopURL = new URL(stopApiURL);
        InputStream apiStream = stopURL.openStream();
        return new InputStreamReader(apiStream, StandardCharsets.UTF_8);
    }

    private String getTextFromAPIReader(InputStreamReader apiReader) {
        BufferedReader bufferedReader = new BufferedReader(apiReader);
        return bufferedReader.lines().collect(Collectors.joining());
    }


    //write main method to test your code
    public static void main(String[] args) {
        ApiStopReader asr = new ApiStopReader();
        asr.getStops();
    }
}


