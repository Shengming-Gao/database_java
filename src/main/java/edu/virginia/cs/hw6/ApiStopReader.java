//package edu.virginia.cs.hw6;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class ApiStopReader implements StopReader {
//    private List<Stop> stopsList;
//
//    @Override
//    public List<Stop> getStops() {
//        //returns a list of Stops from the "stops" endpoint
//        //See Stop class for what data you need to extract
//        //You will read these from the endpoints in the ConfigSingleton.
//        //ApiStopReader uses the Stops endpoint using the "stops" value
//        //A note: If there are stop-ids in the JSON file under "routes"
//        //that do not have a matching stop, simply "skip" that Stop, don't create a
//        //Stop object for it, and don't add it to Routes
//        try {
//            ConfigSingleton config = ConfigSingleton.getInstance();
//            String stopsURL = config.getBusStopsURL();
//            InputStream is = stopsURL.getClass().getResourceAsStream(stopsURL);
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//            String jsonText =
//            JSONObject json = new JSONObject(jsonText);
//            JSONArray stops = json.getJSONArray("stops");
//            stopsList = stops.toList().stream().map(o -> {
//                JSONObject stop = (JSONObject) o;
//                return new Stop(stop.getString("stop_id"), stop.getString("stop_name"), stop.getString("stop_lat"), stop.getString("stop_lon"));
//            }).collect(Collectors.toList());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return stopsList;
//
//
//    }
//}
