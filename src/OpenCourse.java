package Teacher;

import Student.Course.CourseData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import sql.StuMysql;
import java.util.List;

public class OpenCourse extends VBox {
    private int tid;
    private TextField idField = new TextField();
    private TextField nameField = new TextField();
    private CheckBox fuzzyCheck = new CheckBox("模糊查询");
    private TextField minCreditField = new TextField();
    private TextField maxCreditField = new TextField();
    private TableView<CourseData> table = new TableView<>();
    private List<CourseData> allCourses;
    private int currentPage = 1;
    private final int pageSize = 5;
    private HBox pagination = new HBox(10);

    public OpenCourse(int tid) {
        this.tid = tid;
        this.setSpacing(20);
        this.setPadding(new Insets(20));

        // 1. Search Box
        VBox searchBox = new VBox(15);
        searchBox.setPadding(new Insets(25));
        searchBox.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        HBox row1 = new HBox(30);
        row1.setAlignment(Pos.CENTER_LEFT);
        row1.getChildren().addAll(
                new Label("课程号"), idField,
                new Label("课程名"), nameField,
                fuzzyCheck);

        HBox row2 = new HBox(30);
        row2.setAlignment(Pos.CENTER_LEFT);
        Button resetBtn = new Button("重置");
        resetBtn.setStyle("-fx-background-color: #1890ff; -fx-text-fill: white;");
        resetBtn.setOnAction(e -> {
            idField.clear();
            nameField.clear();
            fuzzyCheck.setSelected(false);
            minCreditField.clear();
            maxCreditField.clear();
            loadData();
        });

        row2.getChildren().addAll(
                new Label("学分下限"), minCreditField,
                new Label("学分上限"), maxCreditField,
                resetBtn);

        searchBox.getChildren().addAll(row1, row2);

        // 2. Table
        createTable();

        // 3. Pagination
        pagination.setAlignment(Pos.CENTER_LEFT);

        this.getChildren().addAll(searchBox, table, pagination);

        // Search Listeners
        idField.textProperty().addListener((obs, old, newVal) -> loadData());
        nameField.textProperty().addListener((obs, old, newVal) -> loadData());
        fuzzyCheck.selectedProperty().addListener((obs, old, newVal) -> loadData());
        minCreditField.textProperty().addListener((obs, old, newVal) -> loadData());
        maxCreditField.textProperty().addListener((obs, old, newVal) -> loadData());

        loadData();
    }

    private void createTable() {
        table.setPrefHeight(400);

        TableColumn<CourseData, Integer> idCol = new TableColumn<>("课程号");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(150);

        TableColumn<CourseData, String> nameCol = new TableColumn<>("课程名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<CourseData, Integer> creditCol = new TableColumn<>("学分");
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));
        creditCol.setPrefWidth(150);

        TableColumn<CourseData, Void> actionCol = new TableColumn<>("操作");
        actionCol.setPrefWidth(150);
        actionCol.setCellFactory(col -> new TableCell<CourseData, Void>() {
            private final Button openBtn = new Button("开设");
            {
                openBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #1890ff; -fx-cursor: hand;");
                openBtn.setOnAction(e -> {
                    CourseData data = getTableView().getItems().get(getIndex());
                    if (StuMysql.openCourse(tid, data.getId(), "25-春季学期")) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "课程开设成功！");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "课程开设失败（可能已开设）！");
                        alert.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : openBtn);
            }
        });

        table.getColumns().addAll(idCol, nameCol, creditCol, actionCol);
    }

    private void loadData() {
        allCourses = StuMysql.queryAllC(idField.getText(), nameField.getText(), fuzzyCheck.isSelected(),
                minCreditField.getText(), maxCreditField.getText());
        currentPage = 1;
        updatePage();
    }

    private void updatePage() {
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, allCourses.size());
        table.getItems().clear();
        if (start < allCourses.size()) {
            table.getItems().addAll(allCourses.subList(start, end));
        }
        updatePagination();
    }

    private void updatePagination() {
        pagination.getChildren().clear();
        int totalPages = (int) Math.ceil((double) allCourses.size() / pageSize);
        if (totalPages == 0)
            totalPages = 1;

        Button prev = new Button("<");
        prev.setDisable(currentPage == 1);
        prev.setOnAction(e -> {
            currentPage--;
            updatePage();
        });

        Button pageBtn = new Button(String.valueOf(currentPage));
        pageBtn.setStyle("-fx-background-color: #1890ff; -fx-text-fill: white;");

        Button next = new Button(">");
        next.setDisable(currentPage == totalPages);
        next.setOnAction(e -> {
            currentPage++;
            updatePage();
        });

        pagination.getChildren().addAll(prev, pageBtn, next);
    }
}
