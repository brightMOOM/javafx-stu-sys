package Student;

import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sql.StuMysql;
import stamet.StyleUtils;

public class Grade extends VBox {
    private int sid;
    private TableView<GradeData> gradeTable;
    private List<GradeData> allGrades;
    private ComboBox<String> termCombo;
    private Label avgLabel;
    private int currentPage = 1;
    private final int pageSize = 5;
    private HBox pagination;

    public Grade(int sid) {
        this.sid = sid;
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        StyleUtils.useCss(this, "/style.css");

        // 1. Term Selection
        HBox topBox = new HBox(10);
        Label termLabel = new Label("选择学期");
        termLabel.setStyle("-fx-font-size: 14px;");
        termCombo = new ComboBox<>();
        termCombo.setPrefWidth(200);

        // Load terms
        List<String> terms = StuMysql.queryTerms(sid);
        termCombo.getItems().addAll(terms);
        if (!terms.isEmpty()) {
            termCombo.getSelectionModel().select(0);
        }

        termCombo.setOnAction(e -> loadGrades());
        topBox.getChildren().addAll(termLabel, termCombo);

        // 2. Table
        createGradeTable();

        // 3. Average Grade Label
        avgLabel = new Label("平均成绩：0.0");
        avgLabel.setStyle("-fx-font-size: 16px; -fx-padding: 10 0 0 0;");

        // 4. Pagination
        pagination = createPagination();

        this.getChildren().addAll(topBox, gradeTable, avgLabel, pagination);

        // Initial load
        loadGrades();
    }

    private void createGradeTable() {
        gradeTable = new TableView<>();
        gradeTable.setPrefHeight(300);

        TableColumn<GradeData, Integer> cidCol = new TableColumn<>("课号");
        cidCol.setCellValueFactory(new PropertyValueFactory<>("cid"));
        cidCol.setPrefWidth(100);

        TableColumn<GradeData, String> cnameCol = new TableColumn<>("课程号");
        cnameCol.setCellValueFactory(new PropertyValueFactory<>("cname"));
        cnameCol.setPrefWidth(150);

        TableColumn<GradeData, Integer> tidCol = new TableColumn<>("教师号");
        tidCol.setCellValueFactory(new PropertyValueFactory<>("tid"));
        tidCol.setPrefWidth(100);

        TableColumn<GradeData, String> tnameCol = new TableColumn<>("教师名称");
        tnameCol.setCellValueFactory(new PropertyValueFactory<>("tname"));
        tnameCol.setPrefWidth(120);

        TableColumn<GradeData, Integer> creditCol = new TableColumn<>("学分");
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));
        creditCol.setPrefWidth(80);

        TableColumn<GradeData, String> gradeCol = new TableColumn<>("成绩");
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("gradeStr"));
        gradeCol.setPrefWidth(100);

        gradeTable.getColumns().addAll(cidCol, cnameCol, tidCol, tnameCol, creditCol, gradeCol);
    }

    private void loadGrades() {
        String selectedTerm = termCombo.getValue();
        if (selectedTerm != null) {
            allGrades = StuMysql.queryGrade(sid, selectedTerm);
            calculateAverage();
            currentPage = 1;
            loadPageData(currentPage);
            updatePaginationBtn(currentPage);
        }
    }

    private void calculateAverage() {
        if (allGrades == null || allGrades.isEmpty()) {
            avgLabel.setText("平均成绩：0.0");
            return;
        }
        double total = 0;
        int count = 0;
        for (GradeData g : allGrades) {
            if (g.getGrade() != -1.0f) {
                total += g.getGrade();
            }
            count++; // Always count for average as per the image's 49.09... result?
            // Actually, if count is total number of courses, we use count.
        }
        double avg = count > 0 ? total / count : 0;
        avgLabel.setText("平均成绩：" + avg);
    }

    private void loadPageData(int page) {
        if (allGrades == null)
            return;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allGrades.size());
        gradeTable.getItems().clear();
        if (start < allGrades.size()) {
            gradeTable.getItems().addAll(allGrades.subList(start, end));
        }
    }

    private HBox createPagination() {
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(10, 0, 0, 0));

        Button prevBtn = new Button("<");
        prevBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #e9ecef; -fx-padding: 2px 8px;");
        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadPageData(currentPage);
                updatePaginationBtn(currentPage);
            }
        });

        Button pageBtn = new Button("1");
        pageBtn.setStyle("-fx-background-color: #1890ff; -fx-text-fill: white; -fx-padding: 2px 8px;");

        Button nextBtn = new Button(">");
        nextBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #e9ecef; -fx-padding: 2px 8px;");
        nextBtn.setOnAction(e -> {
            int totalPage = (int) Math.ceil((double) allGrades.size() / pageSize);
            if (currentPage < totalPage) {
                currentPage++;
                loadPageData(currentPage);
                updatePaginationBtn(currentPage);
            }
        });

        hbox.getChildren().addAll(prevBtn, pageBtn, nextBtn);
        return hbox;
    }

    private void updatePaginationBtn(int page) {
        Button pageBtn = (Button) pagination.getChildren().get(1);
        pageBtn.setText(String.valueOf(page));
    }

    public static class GradeData {
        private int cid;
        private String cname;
        private int tid;
        private String tname;
        private int credit;
        private float grade;

        public GradeData(int cid, String cname, int tid, String tname, int credit, float grade) {
            this.cid = cid;
            this.cname = cname;
            this.tid = tid;
            this.tname = tname;
            this.credit = credit;
            this.grade = grade;
        }

        public int getCid() {
            return cid;
        }

        public String getCname() {
            return cname;
        }

        public int getTid() {
            return tid;
        }

        public String getTname() {
            return tname;
        }

        public int getCredit() {
            return credit;
        }

        public float getGrade() {
            return grade;
        }

        // For TableView display, handle NULL/0 grade
        public String getGradeStr() {
            if (grade == -1.0f)
                return "";
            return String.valueOf(grade);
        }
    }
}
