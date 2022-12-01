package edu.virginia.cs.hw6;

import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class DatabaseManagerImpl implements DatabaseManager {
    private static final String DATABASE_PATH = "bus_stops.sqlite3";
    private Connection connection;
    ConfigSingleton config = ConfigSingleton.getInstance();

    @Override
    public void connect() {

        //throw IllegalStateException if the Manager is already connected
        if (connection != null) {
            throw new IllegalStateException("The Manager is already connected");
        }

        try {

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Creates the tables Stops, BusLines, and Routes in the database. Throws
     * an IllegalStateException if the tables already exist.
     * <p>
     * This *does not* populate the tables Data. To populate the data, use
     * the methods addStops and addBusLines.
     *
     * @throws IllegalStateException if the tables already exist
     * @throws IllegalStateException if the Manager hasn't connected yet
     */
    @Override
    public void createTables() {
        try {
            if ( connection == null|| connection.isClosed()) {
                throw new IllegalStateException("Connection is closed right now");
            }

            String databaseName = config.getDatabaseFilename();
            String databaseUrl = "jdbc:sqlite:" + databaseName;
            try {
                Class.forName("org.sqlite.JDBC");
                Connection connection = DriverManager.getConnection(databaseUrl);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                Statement statement1 = connection.createStatement();
                //Create table in SQLite
//            String stopsTable = "CREATE TABLE IF NOT EXISTS Stops (ID INTEGER PRIMARY KEY, Name VARCHAR(255), " +
//                    "Latitude DOUBLE, Longitude DOUBLE)";
                String stopsTable = "CREATE TABLE IF NOT EXISTS Stops (ID int(5) NOT NULL, Name VARCHAR(255) NOT NULL, " +
                        "Latitude DOUBLE NOT NULL, Longitude DOUBLE NOT NULL, PRIMARY KEY (ID))";
                statement1.executeUpdate(stopsTable);
                //all the columns should have the not null constraint
                String BusLinesTable = "CREATE TABLE IF NOT EXISTS BusLines (ID int NOT NULL PRIMARY KEY, IsActive BOOLEAN NOT NULL, " +
                        "LongName VARCHAR(255) NOT NULL, ShortName VARCHAR(255) NOT NULL) ";
                Statement statement2 = connection.createStatement();
//            statement2.executeUpdate(BusLinesTable);


                //Statement statement2 = connection.createStatement();
                statement2.executeUpdate(BusLinesTable);


//            Routes: (related to the class Route.java)
//            ID - INTEGER - Primary Key - autoincrement
//            BusLineID - Foreign Key to BusLines.ID - on delete cascade
//            StopID - Foreign Key to Stops.ID - on delete cascade
//            Order - Integer - The order of the Stop (0 indexed)


//            String RoutesTable = "CREATE TABLE IF NOT EXISTS Routes (ID INTEGER PRIMARY KEY NOT NULL," +
//                    "BusLineID INTEGER NOT NULL, " +
//                    "StopID INTEGER NOT NULL, " +
//                    "Order INTEGER NOT NULL, " +
//                    "FOREIGN KEY (BusLineID) REFERENCES BusLines(ID) ON DELETE CASCADE, " +
//                    "FOREIGN KEY (StopID) REFERENCES Stops(ID) ON DELETE CASCADE)";

                String RoutesTable = "CREATE TABLE Routes (ID INTEGER PRIMARY KEY, " +
                        "BusLineID int NOT NULL," +
                        "StopID int NOT NULL," +
                        "\"Order\" int NOT NULL," +
                        "FOREIGN KEY (BusLineID) REFERENCES BusLines(ID) ON DELETE CASCADE," +
                        "FOREIGN KEY (StopID) REFERENCES Stops(ID) ON DELETE CASCADE)";


                Statement statement3 = connection.createStatement();
                statement3.executeUpdate(RoutesTable);
            } catch (SQLException e) {
                throw new IllegalArgumentException("The table already exist");
            }
        }catch (SQLException e){
            throw new IllegalStateException(e);
        }
    }


    @Override
    public void clear() {
        /**
         * Empties all of the tables, but does not delete the tables. I.e.,
         * the table structure is still there, but the data content is emptied.
         *
         * @throws IllegalStateException if the tables don't exist.
         * @throws IllegalStateException if the Manager hasn't connected yet
         */
        try {
            if ( connection == null|| connection.isClosed()) {
                throw new IllegalStateException("Connection is closed right now");
            }
            Statement statement1 = connection.createStatement();
            String stopsTable = "DELETE FROM Stops";
            statement1.executeUpdate(stopsTable);
            String BusLinesTable = "DELETE FROM BusLines";
            Statement statement2 = connection.createStatement();
            statement2.executeUpdate(BusLinesTable);
            String RoutesTable = "DELETE FROM Routes";
            Statement statement3 = connection.createStatement();
            statement3.executeUpdate(RoutesTable);
        } catch (SQLException e) {
            throw new IllegalStateException("The table doesn't exist");
        }

    }

    @Override
    public void deleteTables() {
        /**
         * Deletes the tables Stops, BusLines, and Routes from the database. This
         * removes both the data and the tables themselves.
         *
         * @throws IllegalStateException if the tables don't exist
         * @throws IllegalStateException if the Manager hasn't connected yet
         */
        Statement statement = null;
        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("Connection is closed right now");
            }
            statement = connection.createStatement();
            String dropS = "drop table Stops";
            statement.executeUpdate(dropS);

            Statement statement1 = connection.createStatement();
            String dropB = "drop table BusLines";
            statement1.executeUpdate(dropB);

            Statement statement2 = connection.createStatement();
            String dropR = "drop table Routes";
            statement2.executeUpdate(dropR);

        }catch(SQLException e){
            throw new IllegalStateException("The tables don't exist");
        }
    }

    @Override
    public void addStops(List<Stop> stopList) {
        /**
         * Add the stopList to the Stops table in the Database.
         *
         * @throws IllegalStateException if Stops table doesn't exist
         * @throws IllegalArgumentException if you add a stop that is already
         * in the database.
         * @throws IllegalStateException if the Manager hasn't connected yet
         */
        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("The Manager hasn't connected yet");
            }
            //check if the table exists
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "Stops", null);
            //insert query
            //Loop through the stopList and insert each stop into the database using String.format
            for (Stop stop : stopList) {
                //check if the stop is already in the database
                Statement statement1 = connection.createStatement();
                String sql = String.format("Select * From Stops where ID = %d", stop.getId());
                ResultSet rs = statement1.executeQuery(sql);
                if (!rs.isClosed()){
                    throw new IllegalArgumentException("you add a stop that is already in the database");
                }

                String insertQuery = String.format("INSERT INTO Stops (ID, Name, Latitude, Longitude) " +
                                "VALUES (%d, \"%s\", %f, %f)", stop.getId(), stop.getName(),
                        stop.getLatitude(), stop.getLongitude());
                Statement statement = connection.createStatement();
                statement.executeUpdate(insertQuery);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Stops table does not exist");
        }
    }

    @Override
    public List<Stop> getAllStops() {
        /**
         * Return a list of all the Stops in the database
         *
         * Returns an empty list if the Stops table is empty.
         *
         * @throws IllegalStateException if Stops doesn't exist
         * @throws IllegalStateException if the Manager hasn't connected yet
         */
        List<Stop> stopList = new ArrayList<>();

        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("The Manager hasn't connected yet");
            }
            //select query to get all the stops and return a list of stops
//        if(connection.isClosed()) {
//            throw new IllegalStateException("The Manager hasn't connected yet");
//        }
            Statement statement = connection.createStatement();

            String sql = "SELECT * FROM Stops";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                double latitude = resultSet.getDouble("Latitude");
                double longitude = resultSet.getDouble("Longitude");
                Stop stop = new Stop(id, name, latitude, longitude);
                stopList.add(stop);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Stops Table doesn't exist");
        }

        return stopList;

    }

    @Override
    public Stop getStopByID(int id) {
        /**
         * Get a specific Stop by ID number;
         *
         * @throws IllegalStateException if Stops table doesn't exist
         * @throws IllegalArgumentException if no Stop with given id found
         * @throws IllegalStateException if the Manager hasn't connected yet
         */
        //select query to get the stop with the given id
        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("The Manager hasn't connected yet");
            }
            try {
                Statement statement1 = connection.createStatement();
                String sql2 = String.format("Select * from Stops");
                ResultSet rs = statement1.executeQuery(sql2);
            }catch (SQLException e) {
                throw new IllegalStateException("Stops table does not exist");
            }

            Statement statement = connection.createStatement();
            String sql = String.format("SELECT * FROM Stops WHERE ID = %d", id);
            ResultSet resultSet = null;
            resultSet = statement.executeQuery(sql);


            if (resultSet.isClosed()) {
                throw new IllegalArgumentException("No Stop with given id found");
            }
            if (resultSet.next()) {
                int stopID = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                double latitude = resultSet.getDouble("Latitude");
                double longitude = resultSet.getDouble("Longitude");
                Stop stop = new Stop(stopID, name, latitude, longitude);
                return stop;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return null;
    }


    @Override
    public Stop getStopByName(String substring) {
        /**
         * Get a specific Stop by name.
         /**
         * Get a specific stop containing the substring.
         *
         * If multiple stops contain the substring, return the one with
         * the smallest ID number.
         * @throws IllegalStateException if Stops table doesn't exist
         * @throws IllegalArgumentException if no Stop with given name found
         * @throws IllegalStateException if the Manager hasn't connected yet
         */
        //Loop through the Stops table and stores the stops in a list of stops
        //Loop through the list of stops and check if the name contains the substring using .contains()
        //If it does, return the stop wih the smallest ID number
        List<Stop> stopList = new ArrayList<>();

        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("The Manager hasn't connected yet");
            }

            try {
                Statement statement1 = connection.createStatement();
                String sql1 = "SELECT * FROM Stops";
                ResultSet rs = statement1.executeQuery(sql1);
            } catch(SQLException e){
                throw new IllegalStateException("Stops table doesn't exist");
            }

            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM Stops";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String name = resultSet.getString("Name");
                double latitude = resultSet.getDouble("Latitude");
                double longitude = resultSet.getDouble("Longitude");
                Stop stop = new Stop(id, name, latitude, longitude);
                stopList.add(stop);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (Stop stop : stopList) {
            if (stop.getName().contains(substring)) {
                return stop;
            }
        }
        throw new IllegalArgumentException("no Stop containing given subString found");
    }

    @Override
    public void addBusLines(List<BusLine> busLineList) {
        /**
         * Add each BusLine in busLineList to the database. This must be called
         * only AFTER the Stops are added, as this will populate both the BusLines
         * and Routes Table.
         *
         * @throws IllegalStateException if Stops table doesn't exist OR is empty
         * @throws IllegalStateException if Routes and BusLines tables don't exist
         * @throws IllegalArgumentException if adding a bus that already exists (i.e., has
         * a matching ID with an existing bus).
         * @throws IllegalArgumentException if adding a Stop to a bus's Route that doesn't
         * exist already.
         * @throws IllegalStateException if the Manager hasn't connected yet
         */
//        if(connection == null || connection.isClosed() )  {
//            throw new IllegalStateException("Error: manager is not connected. ");
//        }

        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("The Manager hasn't connected yet");
            }

            ResultSet rs;
            try {
                Statement statement1 = connection.createStatement();
                String sql1 = "SELECT * FROM Stops";
                rs = statement1.executeQuery(sql1);
            } catch(SQLException e){
                throw new IllegalStateException("Stops table doesn't exist");
            }
            if (rs.isClosed()){
                throw new IllegalStateException("Stops table is empty");
            }

            try {
                Statement statement2 = connection.createStatement();
                String sql1 = "SELECT * FROM Routes";
                ResultSet rs1 = statement2.executeQuery(sql1);
            } catch(SQLException e){
                throw new IllegalStateException(" Routes table doesn't exist");
            }

            try {
                Statement statement3 = connection.createStatement();
                String sql1 = "SELECT * FROM BusLines";
                ResultSet rs2 = statement3.executeQuery(sql1);
            } catch(SQLException e){
                throw new IllegalStateException("Busline table doesn't exist");
            }




            for (BusLine busLine : busLineList) {
                //If adding a bus already exists and has a matching ID

                String insertQuery = String.format("INSERT INTO BusLines (ID, IsActive, LongName, ShortName) " +
                                "VALUES (%d, %b, \"%s\", \"%s\")", busLine.getId(), busLine.isActive(), busLine.getLongName(),
                        busLine.getShortName());
                Statement statement = null;
                try {
                    statement = connection.createStatement();
                    statement.executeUpdate(insertQuery);

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            int count = 1;
            //populate the Route table here, use a count variable to keep track of the order of the stops
//        For example, imagine we had a BusLine with ID 100 with a Route:
//        stopList =  [stopC, stopA, stopB]
//        We would insert the following into the Route table:
//        (100, stopC, 0), (100, stopA, 1), (100, stopB, 2)
            for (BusLine busLine : busLineList) {
                Route r = busLine.getRoute();
                //Using the get(int index) method of the Route class, get the stops in the order they are in the Route
                //and insert them into the Route table
                for (int i = 0; i < r.size(); i++) {
                    Stop stop = r.get(i);
                    String insertQuery = String.format("INSERT INTO Routes (ID, BusLineID, StopID,\"Order\") " +
                            "VALUES (%d, %d, %d, %d)", null, busLine.getId(), stop.getId(), i);
                    Statement statement = null;
                    try {
                        statement = connection.createStatement();
                        statement.executeUpdate(insertQuery);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    count++;
                }
            }
        } catch (SQLException e){
            throw new IllegalStateException(e);
        }
    }


    @Override
    public List<BusLine> getBusLines() {
        //select query to get all the stops and return a list of stops
        List<BusLine> busLineList = new ArrayList<>();
//        if(connection.isClosed()) {
//            throw new IllegalStateException("The Manager hasn't connected yet");
//        }

        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("The Manager hasn't connected yet");
            }

            Statement statement = connection.createStatement();


            try {
                Statement statement1 = connection.createStatement();
                String sql1 = "SELECT * FROM Stops";
                ResultSet rs = statement1.executeQuery(sql1);
            } catch(SQLException e){
                throw new IllegalStateException("Stops, Routes, or Busline table doesn't exist");
            }

            try {
                Statement statement2 = connection.createStatement();
                String sql1 = "SELECT * FROM Routes";
                ResultSet rs = statement2.executeQuery(sql1);
            } catch(SQLException e){
                throw new IllegalStateException("Stops, Routes, or Busline table doesn't exist");
            }

            try {
                Statement statement3 = connection.createStatement();
                String sql1 = "SELECT * FROM BusLines";
                ResultSet rs = statement3.executeQuery(sql1);
            } catch(SQLException e){
                throw new IllegalStateException("Stops, Routes, or Busline table doesn't exist");
            }



            String sql = "SELECT * FROM BusLines";
            try {
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    int id = resultSet.getInt("ID");
                    boolean isActive = resultSet.getBoolean("IsActive");
                    String longName = resultSet.getString("LongName");
                    String shortName = resultSet.getString("ShortName");
                    BusLine busLine = new BusLine(id, isActive, longName, shortName);
                    busLineList.add(busLine);

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return busLineList;
        }catch(SQLException e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public BusLine getBusLineById(int id) {
        /**
         * Get a specific BusLine by its ID number. The returned object must have
         * a fully populated Route object (including all Stops on the route IN-ORDER)
         *
         * @throws IllegalStateException if BusLines doesn't exist OR is empty
         * @throws IllegalArgumentException if no BusLine with that id is found
         * @throws IllegalStateException if the Manager hasn't connected yet
         */

        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("The Manager hasn't connected yet");
            }

            Statement statement = connection.createStatement();
            String sql = String.format("SELECT * FROM BusLines WHERE ID = %d", id);
            ResultSet resultSet = null;
            resultSet = statement.executeQuery(sql);

            ResultSet rs;
            try {
                Statement statement3 = connection.createStatement();
                String sql1 = "SELECT * FROM BusLines";
                rs = statement3.executeQuery(sql1);
            } catch(SQLException e){
                throw new IllegalStateException("BusLine doesn't exist");
            }

            if(rs.isClosed()){
                throw new IllegalStateException("BusLine is empty");
            }

            try {
                if (resultSet.next()) {
                    int busLineID = resultSet.getInt("ID");
                    boolean isActive = resultSet.getBoolean("IsActive");
                    String longName = resultSet.getString("LongName");
                    String shortName = resultSet.getString("ShortName");
                    //get the route from the ApiBusLineReader
                    String sql2 = String.format("SELECT * FROM Routes WHERE BusLineID = %d", busLineID);
                    ResultSet resultSet2 = statement.executeQuery(sql2);
                    //create a new Route object and add the stops to it
                    Route route = new Route();
                    List<Stop> routeList = new ArrayList<>();
                    while (resultSet2.next()) {
                        //get the stops by calling getStopById
                        int stopID = resultSet2.getInt("StopID");
                        Stop stop = getStopByID(stopID);
                        route.addStop(stop);
                    }

                    BusLine busLine = new BusLine(busLineID, isActive, longName, shortName, route);
                    return busLine;
                } else {
                    throw new IllegalArgumentException("No BusLine with given id found");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch(SQLException e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public BusLine getBusLineByLongName(String longName) {
        /**
         * Get a specific BusLine by its long name. The returned object must have
         * a fully populated Route object (including all Stops on the route IN-ORDER)
         *
         * @throws IllegalStateException if BusLines doesn't exist OR is empty
         * @throws IllegalArgumentException if no BusLine with that long name is found
         * @throws IllegalStateException if the Manager hasn't connected yet
         */
        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("Manager hasn't connected yet. ");
            }
            ResultSet resultSet;
            ResultSet rs;
            try{
                Statement statement = connection.createStatement();
                String sql = String.format("SELECT * FROM BusLines");
                rs = statement.executeQuery(sql);
            }catch(SQLException e) {
                throw new IllegalStateException("Busline Table doesn't exist");
            }
            if(rs.isClosed()){
                throw new IllegalStateException("Busline Table is empty");
            }
                Statement statement1 = connection.createStatement();
                String sql = String.format("SELECT * FROM BusLines WHERE LongName = \"%s\" COLLATE NOCASE", longName);
                resultSet = statement1.executeQuery(sql);

            if(resultSet.isClosed()){
                throw new IllegalStateException("no BusLine with that long name is found");
            }

        try {
            if (resultSet.next()) {

                Statement statement2 =connection.createStatement();
                int ID = resultSet.getInt("ID");
                boolean isActive = resultSet.getBoolean("IsActive");
                String shortName = resultSet.getString("ShortName");
               //find the matched BusLineID in the Route table
                String sql2 = String.format("SELECT * FROM Routes WHERE BusLineID = %d", ID);
                ResultSet resultSet2 = statement2.executeQuery(sql2);
                //create a new Route object and add the stops to it
                Route route = new Route();
                List<Stop> routeList = new ArrayList<>();
                while (resultSet2.next()) {
                    //get the stops by calling getStopById
                    int stopID = resultSet2.getInt("StopID");
                    Stop stop = getStopByID(stopID);
                    route.addStop(stop);
                }


                BusLine busLine = new BusLine(ID, isActive, longName, shortName, route);
                return busLine;
            } else {
                throw new IllegalArgumentException("No BusLine with given longName found");
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }catch (SQLException e) {
        throw new RuntimeException(e);
    }
    }

    @Override
    public BusLine getBusLineByShortName(String shortName) {
        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("Manager hasn't connected yet. ");
            }
            ResultSet rs;
            try{
             Statement statement = connection.createStatement();
             String sql = String.format("Select * from BusLines");
             rs = statement.executeQuery(sql);
            }catch(SQLException e){
                throw new IllegalStateException("Busline Table doesn't exist");
            }
            if(rs.isClosed()){
                throw new IllegalStateException("Busline Table is empty");
            }
            Statement statement1 = connection.createStatement();
            String sql = String.format("SELECT * FROM BusLines WHERE shortName = \"%s\" COLLATE NOCASE", shortName);
            ResultSet resultSet;
            resultSet = statement1.executeQuery(sql);
            if(resultSet.isClosed()){
                throw new IllegalStateException("no Busline with short name is found");
            }

            try {
                if (resultSet.next()) {
                    Statement statement2 = connection.createStatement();
                    int busLineID = resultSet.getInt("ID");
                    boolean isActive = resultSet.getBoolean("IsActive");
                    String longName = resultSet.getString("LongName");
                    String sql2 = String.format("SELECT * FROM Routes WHERE BusLineID = %d", busLineID);
                    ResultSet resultSet2 = statement2.executeQuery(sql2);
                    //create a new Route object and add the stops to it
                    Route route = new Route();
                    List<Stop> routeList = new ArrayList<>();
                    while (resultSet2.next()) {
                        //get the stops by calling getStopById
                        int stopID = resultSet2.getInt("StopID");
                        Stop stop = getStopByID(stopID);
                        route.addStop(stop);
                    }
                    BusLine busLine = new BusLine(busLineID, isActive, longName, shortName, route);
                    return busLine;
                } else {
                    throw new IllegalArgumentException("No BusLine with given shortName found");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        /**
         * Commits any changes and ends the connection.
         *
         * @throws IllegalStateException if the Manager hasn't connected yet
         */
        //commit any changes
        try {
            if (connection == null || connection.isClosed()) {
                throw new IllegalStateException("Manager hasn't connected yet");
            }
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
