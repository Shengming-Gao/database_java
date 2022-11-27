package edu.virginia.cs.hw6;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbm = new DatabaseManagerImpl();
        dbm.connect();
       // dbm.deleteTables();
        dbm.createTables();
        //test addStops
//        ApiStopReader asr = new ApiStopReader();
//        List<Stop> stopList = asr.getStops();
//        dbm.addStops(stopList);
        //test addBusLines
//        ApiBusLineReader ablr = new ApiBusLineReader();
//        List<BusLine> busLineList = ablr.getBusLines();
//        dbm.addBusLines(busLineList);
        //test getAllStops
//        List<Stop> allStops = dbm.getAllStops();
        //test getAllBusLines
//        List<BusLine> allBusLines = dbm.getBusLines();
        dbm.disconnect();
    }
}
