package edu.virginia.cs.hw6;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;


public class ConfigSingleton {
    private static final String configurationFileName = "config.json";
    private static ConfigSingleton instance;
    private String busStopsURL;
    private String busLinesURL;
    private String databaseName;

    private ConfigSingleton() {
        setFieldsFromJSON();
    }

    public static ConfigSingleton getInstance() {
        if (instance == null) {
            instance = new ConfigSingleton();
        }
        return instance;
    }

    public String getBusStopsURL() {
        return busStopsURL;
    }

    public String getBusLinesURL() {
        return busLinesURL;
    }

    public String getDatabaseFilename() {
        return databaseName;
    }

    private void setFieldsFromJSON() {
        //TODO: Population the three fields from the config.json file
//        This will be done by reading the config.json file in the resources folder and setting the values of the fields:
//        busStopsURL (with the "stops" endpoint value)
//        busLinesURL (with the "lines" endpoint value)
//        databaseName (with the "database" value)
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            String fileName = classLoader.getResource("edu.virginia.cs.hw6/" + configurationFileName).getFile();
            FileReader fileReader = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append('\n');
                line = br.readLine();
            }
            String text = sb.toString();
            JSONObject o = new JSONObject(text);
            JSONObject endpoints = o.getJSONObject("endpoints");
            busStopsURL = endpoints.getString("stops");
            busLinesURL = endpoints.getString("lines");
            databaseName = o.getString("database");
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
