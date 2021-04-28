package com.baddragon.controllers;

import com.baddragon.Main;
import com.baddragon.database.Connector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportsController {
    static public Stage stage;
    @FXML
    private TextArea taPreview;
    String csvExport;

    @FXML
    void cars(ActionEvent event) {
        taPreview.clear();
        Connection connection = Connector.getInstance().getConnection();
        String sqlStament = "select * from avg_cost_func();";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStament);
            ResultSet resultSet = statement.executeQuery();
            String result = "Car number \taverage price\n";
            csvExport = "car;avg cost\n";
            while (resultSet.next()) {
                String num = resultSet.getString("num");
                double avgCost = resultSet.getFloat("avg_price");
                avgCost = Math.round(avgCost * 100.0) / 100.0;
                result = result.concat("\t" + num + " \t" + avgCost + "\n");
                csvExport = csvExport.concat(num + ";" + avgCost + "\n");
            }
            taPreview.setText(result);
            resultSet.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    void salary(ActionEvent event) {
        taPreview.clear();

        Connection connection = Connector.getInstance().getConnection();
        Map<Integer, String> masters = new HashMap<>();
        String sqlStament = "select * from masters";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStament);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                masters.put(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                );
            }
            resultSet.close();
            String result = "\tMaster \tsalary\n";
            for (Integer id : masters.keySet()) {

                sqlStament = "select salary(?, 5000);";
                statement = connection.prepareStatement(sqlStament);
                statement.setInt(1, id);
                resultSet = statement.executeQuery();
                csvExport = "\tMaster;\tsalary";

                while (resultSet.next()) {
                    String salary = resultSet.getString("salary");
                    result = result.concat("\t" + masters.get(id) + "\t" + salary + "\n");
                    csvExport = csvExport.concat(masters.get(id) + ";" + salary + "\n");
                }
            }
            taPreview.setText(result);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @FXML
    void export(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            saveTextToFile(csvExport, file);
        }
    }

    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
