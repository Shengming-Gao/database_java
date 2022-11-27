package edu.virginia.cs.hw6;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class DatabaseManagerImpl implements DatabaseManager {
    private static final String DATABASE_PATH = "bus_stops.sqlite3";
    private Connection connection;

    ConfigSingleton config = ConfigSingleton.getInstance();

    @Override
    public void connect() {
        /**
         * Establishes the database connection. Must be called before any other
         * methods are called.
         *
         * @throws IllegalStateException if the Manager is already connected
         */
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
            statement2.executeUpdate(BusLinesTable);

            String RoutesTable = "CREATE TABLE IF NOT EXISTS Routes (ID  int NOT NULL AUTO_INCREMENT, BusLineID int, " +
                    "StopID int, Order int, FOREIGN KEY (BusLineID) REFERENCES BusLines(ID) ON DELETE CASCADE, " +
                    "FOREIGN KEY (StopID) REFERENCES Stops(ID) ON DELETE CASCADE, PRIMARY KEY (ID))";
            Statement statement3 = connection.createStatement();
            statement3.executeUpdate(RoutesTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {

    }

    @Override
    public void deleteTables() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String dropS = "drop table Stops";
        try {
            statement.executeUpdate(dropS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            if (connection.isClosed()) {
                throw new IllegalStateException("The Manager hasn't connected yet");
            }
            //check if the table exists
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "Stops", null);
            if (tables.next()) {
                // Table exists
                System.out.println("Stops table exists");
            } else {
                // Table does not exist
                throw new IllegalStateException("Stops table doesn't exist");
            }
            //insert query
            //Loop through the stopList and insert each stop into the database using String.format
            for (Stop stop : stopList) {
                //check if the stop is already in the database
                String insertQuery = String.format("INSERT INTO Stops (ID, Name, Latitude, Longitude) " +
                                "VALUES (%d, \"%s\", %f, %f)", stop.getId(), stop.getName(),
                        stop.getLatitude(), stop.getLongitude());
                Statement statement = connection.createStatement();
                statement.executeUpdate(insertQuery);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        //select query to get all the stops and return a list of stops
        List<Stop> stopList = new ArrayList<>();
//        if(connection.isClosed()) {
//            throw new IllegalStateException("The Manager hasn't connected yet");
//        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = "SELECT * FROM Stops";
        try {
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

        return stopList;

    }

    @Override
    public Stop getStopByID(int id) {
        return null;
    }

    @Override
    public Stop getStopByName(String substring) {
        return null;
    }

    @Override
    public void addBusLines(List<BusLine> busLineList) {
        for (BusLine busLine : busLineList) {
            String insertQuery = String.format("INSERT INTO BusLines (ID, IsActive, LongName, ShortName) " +
                            "VALUES (%d, %b, \"%s\", \"%s\")", busLine.getId(), busLine.isActive(), busLine.getLongName(),
                    busLine.getShortName());
            Statement statement = null;
            try {
                statement = connection.createStatement();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                statement.executeUpdate(insertQuery);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<BusLine> getBusLines() {
        //select query to get all the stops and return a list of stops
        List<BusLine> busLineList = new ArrayList<>();
//        if(connection.isClosed()) {
//            throw new IllegalStateException("The Manager hasn't connected yet");
//        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        return null;

    }

    @Override
    public BusLine getBusLineByLongName(String longName) {
        return null;
    }

    @Override
    public BusLine getBusLineByShortName(String shortName) {
        return null;
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
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            if (connection.isClosed()) {
                throw new IllegalStateException("Manager hasn't connected yet");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
