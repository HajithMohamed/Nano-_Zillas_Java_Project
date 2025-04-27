package org.example.mini_project_java.Controllers.Undergraduate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.example.mini_project_java.Models.TimeTable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewTimetable_controller implements Initializable {
    @FXML
    public AnchorPane studentTimeTable;

    @FXML
    public TableView<TimeTable> timetableTable;

    private final ObservableList<TimeTable> timetableData = FXCollections.observableArrayList();

    @FXML
    public TableColumn<TimeTable, String> timePeriod;

    @FXML
    public TableColumn<TimeTable, String> monday;

    @FXML
    public TableColumn<TimeTable, String> tuesday;

    @FXML
    public TableColumn<TimeTable, String> wednesday;

    @FXML
    public TableColumn<TimeTable, String> thursday;

    @FXML
    public TableColumn<TimeTable, String> friday;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure the table columns
        timePeriod.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        monday.setCellValueFactory(new PropertyValueFactory<>("monday"));
        tuesday.setCellValueFactory(new PropertyValueFactory<>("tuesday"));
        wednesday.setCellValueFactory(new PropertyValueFactory<>("wednesday"));
        thursday.setCellValueFactory(new PropertyValueFactory<>("thursday"));
        friday.setCellValueFactory(new PropertyValueFactory<>("friday"));

        // Load timetable data from database
        loadTimetableData();

        // Set items to the table
        timetableTable.setItems(timetableData);
    }

    private void loadTimetableData() {
        // Get all timetable entries
        List<TimeTable> timeTableEntries = TimeTable.getAllTimeTableEntries();

        // Add all entries to the table
        timetableData.addAll(timeTableEntries);
    }

    // Optional method to refresh the table data
    public void refreshTable() {
        timetableData.clear();
        loadTimetableData();
        timetableTable.refresh();
    }
}