package Student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import sql.StuMysql;
import stamet.StyleUtils;

public class Grade extends VBox {
    private final int sid;

    private ComboBox<String> termSelect = new ComboBox<>();
    private TableView<GradeData> gradeTable;
    private Label avgLabel = new Label();

    private List<GradeData> allGrades = new ArrayList<>();
    private int currentPage = 1;
    private final int pageSize = 5;

    public Grade(int sid) {
        this.sid = sid;
        createView();
        loadTerms();
        if (!termSelect.getItems().isEmpty()) {
            termSelect.getSelectionModel().select(0);
            refreshData();
        }
    }

    private void createView() {
        Label title = new Label("成绩管理");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 0 0 10px 0;");

        HBox termBar = new HBox(10);
        Label termLabel = new Label("选择学期");
        termSelect.setPrefWidth(180);
        termBar.getChildren().addAll(termLabel, termSelect);

        gradeTable = new TableView<>();
        gradeTable.setPrefWidth(700);
        gradeTable.setPrefHeight(260);

        TableColumn<GradeData, Integer> cidCol = new TableColumn<>("课号");
        cidCol.setPrefWidth(100);
        cidCol.setCellValueFactory(new PropertyValueFactory<>("cid"));

        TableColumn<GradeData, String> cnameCol = new TableColumn<>("课程号");
        cnameCol.setPrefWidth(120);
        cnameCol.setCellValueFactory(new PropertyValueFactory<>("cname"));

        TableColumn<GradeData, Integer> tidCol = new TableColumn<>("教师号");
        tidCol.setPrefWidth(100);
        tidCol.setCellValueFactory(new PropertyValueFactory<>("tid"));

        TableColumn<GradeData, String> tnameCol = new TableColumn<>("教师名称");
        tnameCol.setPrefWidth(120);
        tnameCol.setCellValueFactory(new PropertyValueFactory<>("tname"));

        TableColumn<GradeData, Integer> creditCol = new TableColumn<>("学分");
        creditCol.setPrefWidth(80);
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));

        TableColumn<GradeData, Double> gradeCol = new TableColumn<>("成绩");
        gradeCol.setPrefWidth(100);
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));

        TableColumn<GradeData, Void> opCol = new TableColumn<>("");
        opCol.setPrefWidth(80);
        opCol.setCellFactory(new Callback<TableColumn<GradeData, Void>, TableCell<GradeData, Void>>() {
            public TableCell<GradeData, Void> call(TableColumn<GradeData, Void> col) {
                return new TableCell<GradeData, Void>() {
                    private final Button viewBtn = new Button("查看");
                    {
                        viewBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #e9ecef; -fx-padding: 2px 8px;");
                        viewBtn.setOnAction(e -> {
                            GradeData g = getTableView().getItems().get(getIndex());
                            System.out.println("查看成绩:" + g.getCname() + "=" + g.getGrade());
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : viewBtn);
                    }
                };
            }
        });

        gradeTable.getColumns().addAll(cidCol, cnameCol, tidCol, tnameCol, creditCol, gradeCol, opCol);

        HBox pagination = createPagination();

        avgLabel.setStyle("-fx-padding: 10px 0 0 0;");

        this.setSpacing(10);
        this.setPadding(new Insets(20));
        this.getChildren().addAll(title, termBar, gradeTable, avgLabel, pagination);
        StyleUtils.useCss(this, "/style.css");

        termSelect.setOnAction(e -> {
            currentPage = 1;
            refreshData();
            updatePaginationBtn(pagination, currentPage);
        });
    }

    private void loadTerms() {
        List<String> terms = StuMysql.queryTermsBySid(sid);
        termSelect.getItems().setAll(terms);
    }

    private void refreshData() {
        String term = termSelect.getSelectionModel().getSelectedItem();
        if (term == null) {
            gradeTable.getItems().clear();
            avgLabel.setText("平均成绩：-");
            return;
        }
        allGrades = StuMysql.queryGradesBySidAndTerm(sid, term);
        loadPageData(currentPage);
        updateAvgLabel();
    }

    private void loadPageData(int page) {
        int from = (page - 1) * pageSize;
        int to = Math.min(from + pageSize, allGrades.size());
        if (from >= to) {
            gradeTable.getItems().clear();
            return;
        }
        gradeTable.getItems().setAll(allGrades.subList(from, to));
    }

    private HBox createPagination() {
        HBox pagination = new HBox(10);
        pagination.setPadding(new Insets(6, 0, 0, 0));

        Button prev = new Button("<");
        prev.setStyle("-fx-background-color: transparent; -fx-border-color: #e9ecef; -fx-padding: 2px 8px;");
        prev.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadPageData(currentPage);
                updatePaginationBtn(pagination, currentPage);
            }
        });

        Button cur = new Button(String.valueOf(currentPage));
        cur.setStyle("-fx-background-color: #1890ff; -fx-text-fill: white; -fx-padding: 2px 8px;");

        Button next = new Button(">");
        next.setStyle("-fx-background-color: transparent; -fx-border-color: #e9ecef; -fx-padding: 2px 8px;");
        next.setOnAction(e -> {
            int totalPage = (int) Math.ceil((double) allGrades.size() / pageSize);
            if (currentPage < totalPage) {
                currentPage++;
                loadPageData(currentPage);
                updatePaginationBtn(pagination, currentPage);
            }
        });

        pagination.getChildren().addAll(prev, cur, next);
        return pagination;
    }

    private void updatePaginationBtn(HBox pagination, int page) {
        pagination.getChildren().set(1, new Button(String.valueOf(page)));
        ((Button) pagination.getChildren().get(1))
                .setStyle("-fx-background-color: #1890ff; -fx-text-fill: white; -fx-padding: 2px 8px;");
    }

    private void updateAvgLabel() {
        List<Double> grades = allGrades.stream()
                .map(GradeData::getGrade)
                .filter(g -> g != null)
                .collect(Collectors.toList());
        if (grades.isEmpty()) {
            avgLabel.setText("平均成绩：-");
            return;
        }
        double sum = 0.0;
        for (double g : grades) sum += g;
        double avg = sum / grades.size();
        avgLabel.setText("平均成绩：" + String.format("%.2f", avg));
    }

    public static class GradeData {
        private int cid;
        private String cname;
        private int tid;
        private String tname;
        private int credit;
        private Double grade;

        public GradeData(int cid, String cname, int tid, String tname, int credit, Double grade) {
            this.cid = cid;
            this.cname = cname;
            this.tid = tid;
            this.tname = tname;
            this.credit = credit;
            this.grade = grade;
        }

        public int getCid() { return cid; }
        public String getCname() { return cname; }
        public int getTid() { return tid; }
        public String getTname() { return tname; }
        public int getCredit() { return credit; }
        public Double getGrade() { return grade; }
    }
}
