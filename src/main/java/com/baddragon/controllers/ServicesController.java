package com.baddragon.controllers;


import com.baddragon.database.Connector;
import com.baddragon.dto.Master;
import com.baddragon.dto.Service;
import com.baddragon.manager.AppManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.util.ResourceBundle;

public class ServicesController implements Initializable {
    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtOur;

    @FXML
    private TextField txtFor;
    @FXML
    private TableView<Service> txServices;
    @FXML
    private TableColumn<Service, Integer> colId;
    @FXML
    private TableColumn<Service, String> colName;
    @FXML
    private TableColumn<Service, Float> colOur;
    @FXML
    private TableColumn<Service, Float> colForeign;

    @FXML
    void delete(ActionEvent event) {
        if (txtId.getText() == null || txtId.getText().isEmpty()) {
            return;
        }
        Integer id = Integer.parseInt(txtId.getText());

        String sqlStatement = "delete from services where id=?";
        Connection connection = Connector.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        refresh();
    }

    @FXML
    void insert(ActionEvent event) {
        if (txtId.getText() == null || txtName.getText() == null || txtFor.getText() == null || txtOur.getText() == null
                || txtId.getText().isEmpty() || txtName.getText().isEmpty() || txtOur.getText().isEmpty() ||
                txtFor.getText().isEmpty()) {
            return;
        }

        String name = txtName.getText();
        Float our = Float.parseFloat(txtOur.getText());
        Float foreign = Float.parseFloat(txtFor.getText());

        String sqlStatemetn = "insert into services (name, cost_our, cost_foregn) values (?,?,?);";

        Connection connection = Connector.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatemetn);
            statement.setString(1, name);
            statement.setFloat(2, our);
            statement.setFloat(3, foreign);
            statement.execute();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        refresh();
    }

    @FXML
    void refresh() {
        showServices();
        ((MainPageController) AppManager.getInstance().findInstance("MainPageController")).updateForm();
    }

    @FXML
    void update(ActionEvent event) {
        if (txtId.getText() == null || txtName.getText() == null || txtFor.getText() == null || txtOur.getText() == null
                || txtId.getText().isEmpty() || txtName.getText().isEmpty() || txtOur.getText().isEmpty() ||
                txtFor.getText().isEmpty()) {
            return;
        }
        Integer id = Integer.parseInt(txtId.getText());
        String name = txtName.getText();
        Float our = Float.parseFloat(txtOur.getText());
        Float foreign = Float.parseFloat(txtFor.getText());

        String sqlStatement = "update services set name=?, cost_our=?, cost_foreign=? where id=?";
        Connection connection = Connector.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setString(1, name);
            statement.setFloat(2, our);
            statement.setFloat(3, foreign);
            statement.setInt(4, id);
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        refresh();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showServices();
    }

    public void showServices() {
        ObservableList<Service> list = loadServices();

        colId.setCellValueFactory(new PropertyValueFactory<Service, Integer>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<Service, String>("name"));
        colOur.setCellValueFactory(new PropertyValueFactory<Service, Float>("cost_our"));
        colForeign.setCellValueFactory(new PropertyValueFactory<Service, Float>("cost_foreign"));

        txServices.setItems(list);
    }

    public ObservableList<Service> loadServices() {
        ObservableList<Service> list = FXCollections.observableArrayList();

        Connection connection = Connector.getInstance().getConnection();
        String statement = "select * from services order by id";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Service service = Service.builder()
                        .id(resultSet.getInt("id"))
                        .name(resultSet.getString("name"))
                        .cost_our(resultSet.getFloat("cost_our"))
                        .cost_foreign(resultSet.getFloat("cost_foreign"))
                        .build();

                list.add(service);
            }
            resultSet.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    @FXML
    void selectRow(MouseEvent event) {
        if (txServices.getSelectionModel().getSelectedItem() != null) {
            Service selectedService = txServices.getSelectionModel().getSelectedItem();
            txtId.setText(selectedService.getId().toString());
            txtName.setText(selectedService.getName());
            txtOur.setText(selectedService.getCost_our().toString());
            txtFor.setText(selectedService.getCost_foreign().toString());
        }
    }
}
