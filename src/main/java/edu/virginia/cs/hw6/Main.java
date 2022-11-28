package edu.virginia.cs.hw6;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbm = new DatabaseManagerImpl();
        dbm.connect();
        //dbm.deleteTables();
//        dbm.createTables();
//
//        ApiStopReader asr = new ApiStopReader();
//        List<Stop> stopList = asr.getStops();
//        dbm.addStops(stopList);
//
//        ApiBusLineReader ablr = new ApiBusLineReader();
//        List<BusLine> busLineList = ablr.getBusLines();
//        dbm.addBusLines(busLineList);
//
//        List<Stop> allStops = dbm.getAllStops();
//
//        List<BusLine> allBusLines = dbm.getBusLines();


      //  Stop stop = dbm.getStopByID(4235106);
        //System.out.println(stop);

        //test getStopByName
//        Stop stop = dbm.getStopByName("George Welsh Way @ Scott Stadium");
//        System.out.println(stop);

        //test getBusLineByID
//        BusLine busLine = dbm.getBusLineById(4013468);
//        System.out.println(busLine);

        //test getBusLineByLongName
//        BusLine busLine = dbm.getBusLineByLongName("Buckingham East CONNECT");
//        System.out.println(busLine);

        //test getBusLinesByShortName
        BusLine busLine = dbm.getBusLineByShortName("BUCK-N");
        System.out.println(busLine);
        dbm.disconnect();
    }
}
