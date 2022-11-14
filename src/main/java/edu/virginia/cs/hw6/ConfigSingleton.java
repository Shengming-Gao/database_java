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
        try {
            String fileName = "config.json";
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append('\n');
                line = br.readLine();
            }
            String text = sb.toString();

            JSONObject o = new JSONObject(text);

            busStopsURL = (String) o.get("stops");
            busLinesURL = (String) o.get("lines");
            databaseName = o.getString("database");
        }
        catch (Exception e) {

        }

    }
}
