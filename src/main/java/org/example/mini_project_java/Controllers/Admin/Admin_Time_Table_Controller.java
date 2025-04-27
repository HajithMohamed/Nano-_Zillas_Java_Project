package org.example.mini_project_java.Controllers.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.mini_project_java.Models.TimeTable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Admin_Time_Table_Controller implements Initializable {

    @FXML private TableView<TimeTableEntry> timeTableView;
    @FXML private TableColumn<TimeTableEntry, String> timeColumn;
    @FXML private TableColumn<TimeTableEntry, String> mondayColumn;
    @FXML private TableColumn<TimeTableEntry, String> tuesdayColumn;
    @FXML private TableColumn<TimeTableEntry, String> wednesdayColumn;
    @FXML private TableColumn<TimeTableEntry, String> thursdayColumn;
    @FXML private TableColumn<TimeTableEntry, String> fridayColumn;
    @FXML private TableColumn<TimeTableEntry, Void> actionColumn;

    @FXML private TextField courseCode;
    @FXML private TextField courseTitle;
    @FXML private TextField lecId;
    @FXML private TextField courseCredit;
    @FXML private TextField courseType;
    @FXML private TextField courseCreditHours;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> timeSlotComboBox;
    @FXML private Button addCourseBtn;

    private final ObservableList<TimeTableEntry> timeTableData = FXCollections.observableArrayList();
    private final TimeTable timeTableModel = new TimeTable();

    private final String[] timeSlots = {
            "8:00 - 10:00",
            "10:00 - 12:00",
            "12:00 - 1:00", // Lunch break
            "1:00 - 3:00",
            "3:00 - 5:00"
    };

    private final String[] days = {
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the time table columns
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        mondayColumn.setCellValueFactory(new PropertyValueFactory<>("monday"));
        tuesdayColumn.setCellValueFactory(new PropertyValueFactory<>("tuesday"));
        wednesdayColumn.setCellValueFactory(new PropertyValueFactory<>("wednesday"));
        thursdayColumn.setCellValueFactory(new PropertyValueFactory<>("thursday"));
        fridayColumn.setCellValueFactory(new PropertyValueFactory<>("friday"));

        // Set up action column with edit/delete buttons
        setupActionColumn();

        // Load data from database
        loadTimeTableFromDatabase();

        // Setup ComboBoxes for day and time slot selection
        dayComboBox.setItems(FXCollections.observableArrayList(days));
        timeSlotComboBox.setItems(FXCollections.observableArrayList(timeSlots));

        // Add course button action
        addCourseBtn.setOnAction(event -> addCourse());

        // Apply special styling for lunch break row
        timeTableView.setRowFactory(tv -> new TableRow<TimeTableEntry>() {
            @Override
            protected void updateItem(TimeTableEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && item.getTimeSlot().equals("12:00 - 1:00")) {
                    getStyleClass().add("lunch-break");
                } else {
                    getStyleClass().removeAll("lunch-break");
                }
            }
        });
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(new Callback<TableColumn<TimeTableEntry, Void>, TableCell<TimeTableEntry, Void>>() {
            @Override
            public TableCell<TimeTableEntry, Void> call(TableColumn<TimeTableEntry, Void> param) {
                return new TableCell<TimeTableEntry, Void>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");

                    {
                        // Configure Edit button
                        editBtn.getStyleClass().add("edit-button");
                        editBtn.setOnAction(event -> {
                            TimeTableEntry entry = getTableView().getItems().get(getIndex());
                            String timeSlot = entry.getTimeSlot();

                            // Determine which day's cell was clicked
                            TableColumn<TimeTableEntry, ?> column = getTableView().getVisibleLeafColumn(0);
                            String day; // Default

                            // Get the day from column ID or text
                            if (column.getText().equals("Monday")) day = "Monday";
                            else if (column.getText().equals("Tuesday")) day = "Tuesday";
                            else if (column.getText().equals("Wednesday")) day = "Wednesday";
                            else if (column.getText().equals("Thursday")) day = "Thursday";
                            else if (column.getText().equals("Friday")) day = "Friday";
                            else {
                                day = "Monday";
                            }

                            // Get the course information based on day
                            String courseInfo = "";
                            switch (day) {
                                case "Monday": courseInfo = entry.getMonday(); break;
                                case "Tuesday": courseInfo = entry.getTuesday(); break;
                                case "Wednesday": courseInfo = entry.getWednesday(); break;
                                case "Thursday": courseInfo = entry.getThursday(); break;
                                case "Friday": courseInfo = entry.getFriday(); break;
                            }

                            // Check if the cell has course data
                            if (courseInfo == null || courseInfo.isEmpty() || courseInfo.equals("LUNCH BREAK")) {
                                showAlert("No course to edit in this time slot.");
                                return;
                            }

                            // Parse the course information
                            String[] courseDetails = courseInfo.split("\n");
                            if (courseDetails.length >= 3) {
                                // Populate form fields with course data
                                courseCode.setText(courseDetails[0]);
                                courseTitle.setText(courseDetails[1]);
                                lecId.setText(courseDetails[2]);

                                // Get additional course details from database
                                TimeTable dbEntry = TimeTable.getTimeTableEntryByTimeSlot(timeSlot);
                                if (dbEntry != null) {
                                    courseCredit.setText(String.valueOf(dbEntry.getCredits()));
                                    courseType.setText(dbEntry.getType());
                                    courseCreditHours.setText(String.valueOf(dbEntry.getCreditHours()));
                                }

                                // Set combo box selections
                                dayComboBox.setValue(day);
                                timeSlotComboBox.setValue(timeSlot);

                                // Change the add button to update mode
                                addCourseBtn.setText("Update Course");
                                addCourseBtn.setOnAction(e -> updateCourse(timeSlot, day));
                            }
                        });

                        // Configure Delete button
                        deleteBtn.getStyleClass().add("delete-button");
                        deleteBtn.setOnAction(event -> {
                            TimeTableEntry entry = getTableView().getItems().get(getIndex());
                            String timeSlot = entry.getTimeSlot();

                            // Show confirmation dialog
                            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmAlert.setTitle("Confirm Deletion");
                            confirmAlert.setHeaderText(null);
                            confirmAlert.setContentText("Are you sure you want to clear this time slot?");

                            confirmAlert.showAndWait().ifPresent(result -> {
                                if (result == ButtonType.OK) {
                                    // Determine which day's cell was clicked
                                    TableColumn<TimeTableEntry, ?> column = getTableView().getVisibleLeafColumn(0);
                                    String day = "Monday"; // Default

                                    // Get the day from column ID or text
                                    if (column.getText().equals("Monday")) day = "Monday";
                                    else if (column.getText().equals("Tuesday")) day = "Tuesday";
                                    else if (column.getText().equals("Wednesday")) day = "Wednesday";
                                    else if (column.getText().equals("Thursday")) day = "Thursday";
                                    else if (column.getText().equals("Friday")) day = "Friday";

                                    // Clear the cell in the UI
                                    switch (day) {
                                        case "Monday": entry.setMonday(""); break;
                                        case "Tuesday": entry.setTuesday(""); break;
                                        case "Wednesday": entry.setWednesday(""); break;
                                        case "Thursday": entry.setThursday(""); break;
                                        case "Friday": entry.setFriday(""); break;
                                    }

                                    // Update the database
                                    boolean success = timeTableModel.deleteCourse(day, timeSlot);
                                    if (success) {
                                        timeTableView.refresh();
                                        showAlert("Course removed successfully!");
                                    } else {
                                        showAlert("Failed to remove course from database.");
                                    }
                                }
                            });
                        });
                    }

                    private final HBox buttonBox = new HBox(5, editBtn, deleteBtn);
                    {
                        buttonBox.setAlignment(Pos.CENTER);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            TimeTableEntry entry = getTableView().getItems().get(getIndex());
                            // Don't show action buttons for lunch break
                            if (entry.getTimeSlot().equals("12:00 - 1:00")) {
                                setGraphic(null);
                            } else {
                                setGraphic(buttonBox);
                            }
                        }
                    }
                };
            }
        });
    }

    private void loadTimeTableFromDatabase() {
        // Clear existing data
        timeTableData.clear();

        // Initialize time slots if first run
        TimeTable.initializeTimeSlots();

        // Get all entries from the database
        List<TimeTable> entries = TimeTable.getAllTimeTableEntries();

        // If no entries exist yet, create the default structure
        if (entries.isEmpty()) {
            populateTimeTable();
        } else {
            // Convert database entries to UI entries
            for (TimeTable entry : entries) {
                TimeTableEntry uiEntry = new TimeTableEntry(
                        entry.getTimeSlot(),
                        entry.getMonday(),
                        entry.getTuesday(),
                        entry.getWednesday(),
                        entry.getThursday(),
                        entry.getFriday()
                );
                timeTableData.add(uiEntry);
            }
        }

        timeTableView.setItems(timeTableData);
    }

    private void populateTimeTable() {
        // Clear existing data
        timeTableData.clear();

        // Add time slots with empty course data initially
        for (String timeSlot : timeSlots) {
            TimeTableEntry entry = new TimeTableEntry(timeSlot, "", "", "", "", "");

            // Set "LUNCH BREAK" for all days during lunch time
            if (timeSlot.equals("12:00 - 1:00")) {
                entry = new TimeTableEntry(timeSlot, "LUNCH BREAK", "LUNCH BREAK", "LUNCH BREAK", "LUNCH BREAK", "LUNCH BREAK");
            }

            timeTableData.add(entry);
        }

        timeTableView.setItems(timeTableData);
    }

    private void addCourse() {
        // Get values from form fields
        String code = courseCode.getText().trim();
        String title = courseTitle.getText().trim();
        String lecturer = lecId.getText().trim();
        String credits = courseCredit.getText().trim();
        String type = courseType.getText().trim();
        String creditHours = courseCreditHours.getText().trim();
        String selectedDay = dayComboBox.getValue();
        String selectedTimeSlot = timeSlotComboBox.getValue();

        // Validate input
        if (code.isEmpty() || title.isEmpty() || lecturer.isEmpty() ||
                credits.isEmpty() || type.isEmpty() || creditHours.isEmpty() ||
                selectedDay == null || selectedTimeSlot == null) {

            showAlert("Please fill in all fields.");
            return;
        }

        // Skip if lunch break is selected
        if (selectedTimeSlot.equals("12:00 - 1:00")) {
            showAlert("Cannot add course during lunch break.");
            return;
        }

        // Create course information
        String courseInfo = code + "\n" + title + "\n" + lecturer;

        // Update the time table
        for (TimeTableEntry entry : timeTableData) {
            if (entry.getTimeSlot().equals(selectedTimeSlot)) {
                switch (selectedDay) {
                    case "Monday":
                        entry.setMonday(courseInfo);
                        break;
                    case "Tuesday":
                        entry.setTuesday(courseInfo);
                        break;
                    case "Wednesday":
                        entry.setWednesday(courseInfo);
                        break;
                    case "Thursday":
                        entry.setThursday(courseInfo);
                        break;
                    case "Friday":
                        entry.setFriday(courseInfo);
                        break;
                }
                break;
            }
        }

        // IMPORTANT: Add this code to update the database
        try {
            // Convert credits and creditHours to integers
            int creditsInt = Integer.parseInt(credits);
            int creditHoursInt = Integer.parseInt(creditHours);

            // Call the TimeTable model to add the course to the database
            boolean success = timeTableModel.addCourse(
                    selectedDay,
                    selectedTimeSlot,
                    code,
                    title,
                    lecturer,
                    creditsInt,
                    type,
                    creditHoursInt
            );

            if (!success) {
                showAlert("Failed to update database. Please try again.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Credits and Credit Hours must be numbers.");
            return;
        }

        // Refresh the table view
        timeTableView.refresh();

        // Clear form fields
        clearFormFields();

        showAlert("Course added successfully!");
    }

    private void updateCourse(String timeSlot, String day) {
        // Get values from form fields
        String code = courseCode.getText().trim();
        String title = courseTitle.getText().trim();
        String lecturer = lecId.getText().trim();
        String credits = courseCredit.getText().trim();
        String type = courseType.getText().trim();
        String creditHours = courseCreditHours.getText().trim();

        // Validate input
        if (code.isEmpty() || title.isEmpty() || lecturer.isEmpty() ||
                credits.isEmpty() || type.isEmpty() || creditHours.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        // Create course information
        String courseInfo = code + "\n" + title + "\n" + lecturer;

        // Update the time table UI
        for (TimeTableEntry entry : timeTableData) {
            if (entry.getTimeSlot().equals(timeSlot)) {
                switch (day) {
                    case "Monday": entry.setMonday(courseInfo); break;
                    case "Tuesday": entry.setTuesday(courseInfo); break;
                    case "Wednesday": entry.setWednesday(courseInfo); break;
                    case "Thursday": entry.setThursday(courseInfo); break;
                    case "Friday": entry.setFriday(courseInfo); break;
                }
                break;
            }
        }

        // Update the database
        try {
            // Convert credits and creditHours to integers
            int creditsInt = Integer.parseInt(credits);
            int creditHoursInt = Integer.parseInt(creditHours);

            // Get the entry ID from the database
            TimeTable dbEntry = TimeTable.getTimeTableEntryByTimeSlot(timeSlot);
            if (dbEntry != null) {
                // Call the TimeTable model to update the course
                timeTableModel.createOrEditTimeTable(
                        timeSlot,
                        day,
                        code,
                        title,
                        lecturer,
                        creditsInt,
                        type,
                        creditHoursInt,
                        true,  // isEdit = true
                        dbEntry.getId()
                );
            } else {
                showAlert("Failed to locate database entry for this time slot.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Credits and Credit Hours must be numbers.");
            return;
        }

        // Refresh the table view
        timeTableView.refresh();

        // Reset add button text and action
        addCourseBtn.setText("Add Course");
        addCourseBtn.setOnAction(e -> addCourse());

        // Clear form fields
        clearFormFields();

        showAlert("Course updated successfully!");
    }

    private void clearFormFields() {
        courseCode.clear();
        courseTitle.clear();
        lecId.clear();
        courseCredit.clear();
        courseType.clear();
        courseCreditHours.clear();
        dayComboBox.getSelectionModel().clearSelection();
        timeSlotComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Data model class for TimeTableEntry
    public static class TimeTableEntry {
        private String timeSlot;
        private String monday;
        private String tuesday;
        private String wednesday;
        private String thursday;
        private String friday;

        public TimeTableEntry(String timeSlot, String monday, String tuesday, String wednesday, String thursday, String friday) {
            this.timeSlot = timeSlot;
            this.monday = monday;
            this.tuesday = tuesday;
            this.wednesday = wednesday;
            this.thursday = thursday;
            this.friday = friday;
        }

        // Getters and Setters
        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

        public String getMonday() { return monday; }
        public void setMonday(String monday) { this.monday = monday; }

        public String getTuesday() { return tuesday; }
        public void setTuesday(String tuesday) { this.tuesday = tuesday; }

        public String getWednesday() { return wednesday; }
        public void setWednesday(String wednesday) { this.wednesday = wednesday; }

        public String getThursday() { return thursday; }
        public void setThursday(String thursday) { this.thursday = thursday; }

        public String getFriday() { return friday; }
        public void setFriday(String friday) { this.friday = friday; }
    }
}