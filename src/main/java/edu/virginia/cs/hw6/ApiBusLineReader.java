//package edu.virginia.cs.hw6;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.*;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class ApiBusLineReader implements BusLineReader {
//    List<BusLine> busLinesList;
//
//    //constructor
//    public ApiBusLineReader() {
//        busLinesList = new ArrayList<>();
//    }
//
//    ConfigSingleton config = ConfigSingleton.getInstance();
//    public final String busLineApiURL = config.getBusLinesURL();
//
//    @Override
//    public List<BusLine> getBusLines() {
////        ApiBusLineReader uses both:
////        Most of the contents of the BusLine object from the "lines" endpoint
////        The Route must be built using the "routes" value in the "stops" endpoint
////        The routes list the "stops" in-order of how they are visited on the BusLine
////        Be aware that your code will be tested on other data than the endpoints above.
////        However, all JSON examples we use in testing will be formatted identically.
////        A note: If there are stop-ids in the JSON file under "routes" that do not have a matching stop,
////        simply "skip" that Stop, don't create a Stop object for it, and don't add it to Routes
//        try {
//            String jsonText = getJSONText();
//            JSONObject o = new JSONObject(jsonText);
//            JSONArray lines = o.getJSONArray("routes");
//            //store id, isActive,longName, shortName in BusLine
//            for (Object m : lines) {
//                if (m instanceof JSONObject) {
//                    JSONObject line = (JSONObject) m;
//                    int id = line.getInt("id");
//                    boolean isActive = line.getBoolean("is_active");
//                    String longName = line.getString("long_name");
//                    String shortName = line.getString("short_name");
//                    // using the ID, we go to the stops endpoint and get the stops and add them to the stops list
//                 Route route = new Route();
//                    //In the ApiStopReader, we have already created a list of stops.
//                    ApiStopReader stopReader = new ApiStopReader();
//                    List<Stop> allStops = stopReader.getStops();
//                    //find the stop in the list of all stops
//                    for (Stop s : allStops) {
//                        if (s.getId() == id) {
//                            route.addStop(s);
//                        }
//                    }
//                    BusLine b = new BusLine(id, isActive, longName, shortName, route);
//                    busLinesList.add(b);
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return null;
//
//
//    }
//
//    private String getJSONText() throws IOException {
//        InputStreamReader apiReader = getAPIReader();
//        return getTextFromAPIReader(apiReader);
//    }
//
//    private InputStreamReader getAPIReader() throws IOException {
//        URL stopURL = new URL(busLineApiURL);
//        InputStream apiStream = stopURL.openStream();
//        return new InputStreamReader(apiStream, StandardCharsets.UTF_8);
//    }
//
//    private String getTextFromAPIReader(InputStreamReader apiReader) {
//        BufferedReader bufferedReader = new BufferedReader(apiReader);
//        return bufferedReader.lines().collect(Collectors.joining());
//    }
//
//}
