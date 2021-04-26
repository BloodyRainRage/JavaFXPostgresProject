package com.baddragon.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {

    protected String url = "jdbc:postgresql://localhost/dblabs?user=baddragon&password=524656bnm";

    private static Connector instance = new Connector();

    private Connector(){

    }

    static public Connector getInstance(){
        return instance;
    }

    public Connection getConnection(){
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

}
