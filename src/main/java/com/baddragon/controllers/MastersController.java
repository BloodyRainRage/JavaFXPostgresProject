package com.baddragon.controllers;

import com.baddragon.database.Connector;
import com.baddragon.dto.Car;
import com.baddragon.dto.Master;
import com.baddragon.dto.Record;
import com.baddragon.manager.AppManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class MastersController implements Initializable {

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TableView<Master> tvMasters;

    @FXML
    private TableColumn<Master, Integer> idCol;

    @FXML
    private TableColumn<Master, String> colName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AppManager.getInstance().addController("MastersController", this);
        showMasters();
    }

    public void showMasters() {
        ObservableList<Master> list = loadMasters();

        idCol.setCellValueFactory(new PropertyValueFactory<Master, Integer>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<Master, String>("name"));

        tvMasters.setItems(list);
    }

    private ObservableList<Master> loadMasters() {
        ObservableList<Master> list = FXCollections.observableArrayList();

        Connection connection = Connector.getInstance().getConnection();
        String statement = "select * from masters order by id";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Master master = Master.builder()
                        .id(resultSet.getInt("id"))
                        .name(resultSet.getString("name"))
                        .build();

                list.add(master);
            }
            resultSet.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return list;
    }

    public void refresh() {
        showMasters();
        ((MainPageController) AppManager.getInstance().findInstance("MainPageController")).updateForm();
    }

    public void insert() {

        if (txtId.getText() == null || txtId.getText().isEmpty()
                || txtName.getText() == null || txtName.getText().isEmpty()
        ) {
            return;
        }

        String name = txtName.getText();

        Connection connection = Connector.getInstance().getConnection();

        String sqlStatement = "insert into masters (name) values (?);";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setString(1, name);
            statement.execute();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        refresh();
    }

    public void update() {
        if (txtId.getText() == null || txtId.getText().isEmpty()
                || txtName.getText() == null || txtName.getText().isEmpty()
        ) {
            return;
        }

        Integer id = Integer.parseInt(txtId.getText());
        String name = txtName.getText();

        String sqlStatement = "update masters set name=? where id=?";

        Connection connection = Connector.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setString(1, name);
            statement.setInt(2, id);
            statement.execute();

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        refresh();
    }

    public void delete() {
        if (txtId.getText() == null || txtId.getText().isEmpty()
                || txtName.getText() == null || txtName.getText().isEmpty()
        ) {
            return;
        }

        Integer id = Integer.parseInt(txtId.getText());
        String sqlStatement = "delete from masters where id=?";
        Connection connection = Connector.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setInt(1, id);
            statement.execute();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        refresh();
    }

    @FXML
    void selectRow(MouseEvent event) {
        if (tvMasters.getSelectionModel().getSelectedItem() != null) {
            Master selectedMaster = tvMasters.getSelectionModel().getSelectedItem();
            txtId.setText(selectedMaster.getId().toString());
            txtName.setText(selectedMaster.getName());
        }
    }

}
