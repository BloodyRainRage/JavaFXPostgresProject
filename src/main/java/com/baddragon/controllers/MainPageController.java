package com.baddragon.controllers;

import com.baddragon.database.Connector;
import com.baddragon.dto.Record;
import com.baddragon.manager.AppManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    @FXML
    private TableView<Record> tableWorks;
    @FXML
    private TableColumn<Record, Integer> colId;
    @FXML
    private TableColumn<Record, Date> colDate;
    @FXML
    private TableColumn<Record, String> colMaster;
    @FXML
    private TableColumn<Record, String> colCarNum;
    @FXML
    private TableColumn<Record, String> colCarMark;
    @FXML
    private TableColumn<Record, String> colCarCol;
    @FXML
    private TableColumn<Record, Boolean> colCarIsFor;
    @FXML
    private TableColumn<Record, String> colServName;
    @FXML
    private TableColumn<Record, Float> colServCost;
    @FXML
    private TextField textId;
    @FXML
    private DatePicker dpDate;
    @FXML
    private ComboBox<String> cbMaster;
    @FXML
    private ComboBox<String> cbCar;
    @FXML
    private ComboBox<String> cbService;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnRefresh;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AppManager.getInstance().addController("MainPageController", this);
        updateForm();
    }

    public void updateForm(){
        loadMasters();
        loadCars();
        loadServices();
        showRecords();
    }

    @FXML
    void delete(ActionEvent event) {
        if (areComboNotFilled()) {
            setStatusAsFail(true);
            return;
        }

        Integer id = Integer.parseInt(textId.getText());

        String sqlStatement = "delete from works where id=?";

        Connection connection = Connector.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setInt(1, id);

            setStatusAsFail(statement.execute());
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            setStatusAsFail(true);
            throwables.printStackTrace();
        }
        showRecords();
    }

    @FXML
    void insert(ActionEvent event) {
        if (areComboNotFilled()) {
            setStatusAsFail(true);
            return;
        }
        Date date;
        if (dpDate.getValue() == null) {
            date = new Date();
        } else {
            date = Date.from(dpDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        String master = cbMaster.getValue();
        String car = cbCar.getValue();
        String service = cbService.getValue();

        Connection connection = Connector.getInstance().getConnection();
        String sqlStatement = "insert into works (date_work, master_id, car_id, service_id) values " +
                "(?," +
                "(select id from masters where name=?)," +
                "(select id from cars where num=?)," +
                "(select id from services where name=?));";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setDate(1, sqlDate);
            statement.setString(2, master);
            statement.setString(3, car);
            statement.setString(4, service);

            setStatusAsFail(statement.execute());
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            setStatusAsFail(true);
            throwables.printStackTrace();
        }
        showRecords();
    }

    /**
     * Sets label as success when false, and failure when true
     *
     * @param status status of sql execute
     */
    public void setStatusAsFail(Boolean status) {
        if (status) {
            lblStatus.setText("Last operation: failure");
        } else {
            lblStatus.setText("Last operation: success");
        }
    }

    @FXML
    void refresh(ActionEvent event) {
        showRecords();
    }

    @FXML
    void update(ActionEvent event) {

        if (areComboNotFilled()) {
            setStatusAsFail(true);
            return;
        }

        Integer id = Integer.parseInt(textId.getText());
        LocalDate date;
        Timestamp ts;
        if (dpDate.getValue() == null) {
            date = (new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            date = LocalDate.of(
                    dpDate.getValue().getYear(),
                    dpDate.getValue().getMonth(),
                    dpDate.getValue().getDayOfMonth()
            );
        }

        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        ts = new Timestamp(sqlDate.getTime());
        String master = cbMaster.getValue();
        String car = cbCar.getValue();
        String service = cbService.getValue();

        String sqlStatement = "update works set date_work=?, master_id=(select id from masters where name=?)," +
                "car_id=(select id from cars where num=?)," +
                "service_id=(select id from services where name=?)" +
                "where id=?;";

        Connection connection = Connector.getInstance().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setTimestamp(1, ts);
            statement.setString(2, master);
            statement.setString(3, car);
            statement.setString(4, service);
            statement.setInt(5, id);

            setStatusAsFail(statement.execute());
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            setStatusAsFail(true);
            throwables.printStackTrace();
        }
        showRecords();
    }

    public void loadMasters() {
        Connection connection = Connector.getInstance().getConnection();
        String sqlStatement = "select id, name from masters order by name asc";
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString("name"));
            }
            cbMaster.setItems(list);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadCars() {
        Connection connection = Connector.getInstance().getConnection();
        String sqlStatement = "select id, num from cars order by num asc";
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString("num"));
            }
            cbCar.setItems(list);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void loadServices() {
        Connection connection = Connector.getInstance().getConnection();
        String sqlStatement = "select id, name from services order by name asc";
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString("name"));
            }
            cbService.setItems(list);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void showRecords() {
        ObservableList<Record> list = loadRecords();

        colId.setCellValueFactory(new PropertyValueFactory<Record, Integer>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<Record, Date>("date"));
        colMaster.setCellValueFactory(new PropertyValueFactory<Record, String>("master"));
        colCarNum.setCellValueFactory(new PropertyValueFactory<Record, String>("carNumber"));
        colCarMark.setCellValueFactory(new PropertyValueFactory<Record, String>("carModel"));
        colCarCol.setCellValueFactory(new PropertyValueFactory<Record, String>("carColor"));
        colCarIsFor.setCellValueFactory(new PropertyValueFactory<Record, Boolean>("isForeign"));
        colServName.setCellValueFactory(new PropertyValueFactory<Record, String>("serviceName"));
        colServCost.setCellValueFactory(new PropertyValueFactory<Record, Float>("serviceCost"));

        tableWorks.setItems(list);

    }

    private ObservableList<Record> loadRecords() {

        ObservableList<Record> list = FXCollections.observableArrayList();

        Connection connection = Connector.getInstance().getConnection();
        String statement = "select w.id,\n" +
                "       w.date_work,\n" +
                "       m.name master,\n" +
                "       c.num,\n" +
                "       c.mark,\n" +
                "       c.color color,\n" +
                "       c.is_foreign,\n" +
                "       s.name service,\n" +
                "       case c.is_foreign" +
                "       when 0 then (select s.cost_our from services s where w.service_id=s.id)\n" +
                "       when 1 then (select s.cost_foreign from services s where w.service_id=s.id)\n" +
                "            end as cost\n" +
                "from works w\n" +
                "         join masters m on (m.id = w.master_id)\n" +
                "         join cars c on (c.id = w.car_id)\n" +
                "        join services s on (w.service_id = s.id)\n" +
                "order by  w.id asc;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Record record = Record.builder()
                        .id(resultSet.getInt("id"))
                        .date(resultSet.getDate("date_work"))
                        .master(resultSet.getString("master"))
                        .carNumber(resultSet.getString("num"))
                        .carModel(resultSet.getString("mark"))
                        .carColor(resultSet.getString("color"))
                        .isForeign(resultSet.getBoolean("is_foreign"))
                        .serviceName(resultSet.getString("service"))
                        .serviceCost(resultSet.getFloat("cost"))
                        .build();

                list.add(record);

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return list;
    }

    public void getSelectedRow() {
        if (tableWorks.getSelectionModel().getSelectedItem() != null) {
            Record selectedRecord = tableWorks.getSelectionModel().getSelectedItem();
            textId.setText(selectedRecord.getId().toString());
            dpDate.setValue(new java.sql.Date(selectedRecord.getDate().getTime()).toLocalDate());

            cbMaster.setValue(selectedRecord.getMaster());
            cbCar.setValue(selectedRecord.getCarNumber());
            cbService.setValue(selectedRecord.getServiceName());

        }
    }

    public boolean areComboNotFilled() {
        if ((cbCar.getValue() == null || cbMaster.getValue() == null || cbService.getValue() == null)){
            return true;
        }
        if (cbCar.getValue().equals("Car") || cbMaster.getValue().equals("Master")
                || cbService.equals("Service")) {
            return true;
        }
        return false;
    }

    public void onCarsEdit(){
        try {
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/editcars.fxml"));
            Scene scene = new Scene(root, 570, 456);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
