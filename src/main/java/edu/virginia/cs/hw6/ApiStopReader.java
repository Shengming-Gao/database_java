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

public class ApiStopReader implements StopReader {
    private List <Stop> stopsList;
    @Override
    public List<Stop> getStops() {
        ConfigSingleton singleton = ConfigSingleton.getInstance();

        try {
            URL stopsURL = new URL(singleton.getBusStopsURL());
            String jsonStr = getJSONStringFromURL(stopsURL);
            JSONObject stopsJSONObj = new JSONObject(jsonStr);
            JSONArray stopsArr = stopsJSONObj.getJSONArray("stops");

            addStopsToList(stopsList, stopsArr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stopsList;
    }

    private static void addStopsToList(List<Stop> stopsList, JSONArray stopsArr) {
        for(Object o: stopsArr) {
            if(o instanceof JSONObject) {
                int id = ((JSONObject) o).getInt("id");
                String name = ((JSONObject) o).getString("name");
                double latitude = ((JSONObject) o).getJSONArray("position").getDouble(0);
                double longitude = ((JSONObject) o).getJSONArray("position").getDouble(1);

                Stop s = new Stop(id, name, latitude, longitude);
                stopsList.add(s);
            }
        }
    }

    //inspired by BestSellersAPI.java
    private String getJSONStringFromURL(URL stopsURL) throws IOException {
        InputStream in = stopsURL.openStream();
        InputStreamReader inR = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(inR);
        String jsonStr = br.lines().collect(Collectors.joining());
        br.close();
        return jsonStr;
    }
}
