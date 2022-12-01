package edu.virginia.cs.hw6;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbm = new DatabaseManagerImpl();
        dbm.connect();
//        dbm.connect();
     //   dbm.createTables();

      // dbm.createTables();
//        dbm.deleteTables();
//        dbm.createTables();

     //   dbm.clear();


        ApiStopReader asr = new ApiStopReader();
        List<Stop> stopList = asr.getStops();
        dbm.addStops(stopList);
        dbm.addStops(stopList);


        ApiBusLineReader ablr = new ApiBusLineReader();
        List<BusLine> busLineList = ablr.getBusLines();
        dbm.addBusLines(busLineList);

//       List<Stop> allStops = dbm.getAllStops();
//
//        List<BusLine> allBusLines = dbm.getBusLines();
//
//
       // Stop stop = dbm.getStopByID(4235106);
        Stop stop = dbm.getStopByID(23);
        System.out.println(stop);
//
//        test getStopByName
//        Stop stop = dbm.getStopByName("George Welsh Way @ Scott Stadium");
//        stop = dbm.getStopByName("George Welsh Way");
//        System.out.println(stop);

//        //test getBusLineByID
//        BusLine busLine = dbm.getBusLineById(4013468);
//        System.out.println(busLine);
//
//        //test getBusLineByLongName
//        BusLine busLine = dbm.getBusLineByLongName("29 north connect");
//        System.out.println(busLine);
//
//       // test getBusLinesByShortName
//        BusLine busLine = dbm.getBusLineByShortName("red");
//        System.out.println(busLine);
        dbm.disconnect();
    }
}
