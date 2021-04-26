package com.baddragon.controllers;

import com.baddragon.database.Connector;
import com.baddragon.dto.Car;
import com.baddragon.dto.Record;
import com.baddragon.manager.AppManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class CarsController implements Initializable {

    @FXML
    private TableView<Car> tvCars;
    @FXML
    private TableColumn<Car, Integer> colId;
    @FXML
    private TableColumn<Car, String> colCarNum;
    @FXML
    private TableColumn<Car, String> colCarCol;
    @FXML
    private TableColumn<Car, String> colCarMark;
    @FXML
    private TableColumn<Car, Boolean> colCarFor;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNum;
    @FXML
    private TextField txtCol;
    @FXML
    private TextField txtMark;
    @FXML
    private CheckBox checkIsFor;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AppManager.getInstance().addController("CarsController", this);
        showCars();
    }

    @FXML
    void insert(ActionEvent event) {

        if(txtNum.getText().isEmpty() || txtCol.getText().isEmpty() || txtMark.getText().isEmpty()){
            return;
        }

        Connection connection = Connector.getInstance().getConnection();
        String sqlStatement = "insert into cars (num, color, mark, is_foreign) values " +
                "(?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setString(1, txtNum.getText());
            statement.setString(2, txtCol.getText());
            statement.setString(3, txtMark.getText());

            Integer checked;
            if (checkIsFor.isSelected()){
                checked = 1;
            } else {
                checked = 0;
            }
            statement.setInt(4, checked);

            statement.execute();
        } catch (SQLException throwables) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText(throwables.getMessage());
            errorAlert.showAndWait();
            throwables.printStackTrace();
        }
        refresh();
    }

    @FXML
    void refresh() {
        showCars();
        ((MainPageController) AppManager.getInstance().findInstance("MainPageController")).updateForm();
    }

    @FXML
    void selectRow(MouseEvent event) {
        if (tvCars.getSelectionModel().getSelectedItem() != null) {
            Car selectedCar = tvCars.getSelectionModel().getSelectedItem();
            txtId.setText(selectedCar.getId().toString());
            txtNum.setText(selectedCar.getCarNum());
            txtCol.setText(selectedCar.getCarColor());
            txtMark.setText(selectedCar.getCarMark());
            checkIsFor.setSelected(selectedCar.getIsForeign());
            checkIsFor.setSelected(selectedCar.getIsForeign());
        }
    }

    @FXML
    void update(ActionEvent event) {

    }

    public void showCars(){
        ObservableList<Car> list = loadCars();

        colId.setCellValueFactory(new PropertyValueFactory<Car, Integer>("id"));
        colCarNum.setCellValueFactory(new PropertyValueFactory<Car, String>("carNum"));
        colCarCol.setCellValueFactory(new PropertyValueFactory<Car, String>("carColor"));
        colCarMark.setCellValueFactory(new PropertyValueFactory<Car, String>("carMark"));
        colCarFor.setCellValueFactory(new PropertyValueFactory<Car, Boolean>("isForeign"));

        tvCars.setItems(list);
    }

    public ObservableList loadCars(){
        ObservableList<Car> list = FXCollections.observableArrayList();

        Connection connection = Connector.getInstance().getConnection();
        String statement = "select * from cars";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Car car = Car.builder()
                        .id(resultSet.getInt("id"))
                        .carNum(resultSet.getString("num"))
                        .carColor(resultSet.getString("color"))
                        .carMark(resultSet.getString("mark"))
                        .isForeign(resultSet.getBoolean("is_foreign"))
                        .build();

                list.add(car);

            }
            resultSet.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return list;
    }

    public void delete(){
        if (txtId.getText() == null || txtId.getText().isEmpty()){
            return;
        }

        String selectStatement = "select * from works where car_id=?";
        Connection connection = Connector.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(selectStatement);
            statement.setInt(1, Integer.parseInt(txtId.getText()));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Deletion error");
                errorAlert.setContentText("You can not delete this record while it contains in work records");
                errorAlert.showAndWait();
                resultSet.close();
                connection.close();
                return;
            }

            String sqlStatement = "delete from cars where id=?";
            statement = connection.prepareStatement(sqlStatement);
            statement.setInt(1, Integer.parseInt(txtId.getText()));

            statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        refresh();
    }

}
