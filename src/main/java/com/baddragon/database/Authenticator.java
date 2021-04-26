package com.baddragon.database;

import java.sql.*;

public class Authenticator {

    String url = "jdbc:postgresql://localhost/dblabs?user=baddragon&password=524656bnm";

    public boolean loginIsCorrect(String login) throws SQLException {


        try (Connection connection = DriverManager.getConnection(url)) {

            String statement = "select login  from users where login=?";
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, login);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null && resultSet.next()) {
                resultSet.close();
                return true;
            }
        }
        return false;
    }

    public boolean checkLogPassPair(String login, String pass) throws SQLException {

        try (Connection connection = DriverManager.getConnection(url)) {

            String statement = "select login from users where login=? and password=md5(?)";
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null && resultSet.next()) {
                resultSet.close();
                return true;
            }
        }

        return false;
    }

}
