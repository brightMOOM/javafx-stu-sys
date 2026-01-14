package Teacher;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sql.StuMysql;
import sql.StuMysql.TeacherGradeData;

public class TeacherGradeManage extends VBox {
	private int tid;
	private Teacher teacher;
	private TextField sidField = new TextField();
	private TextField snameField = new TextField();
	private CheckBox sFuzzyCheck = new CheckBox("模糊查询");
	private TextField cidField = new TextField();
	private TextField cnameField = new TextField();
	private CheckBox cFuzzyCheck = new CheckBox("模糊查询");
	private TextField minGradeField = new TextField();
	private TextField maxGradeField = new TextField();
	private ComboBox<String> termCombo = new ComboBox<>();
	private TableView<TeacherGradeData> table = new TableView<>();
	private List<TeacherGradeData> allGrades;
	private int currentPage = 1;
	private final int pageSize = 5;
	private HBox pagination = new HBox(10);

	public TeacherGradeManage(int tid, Teacher teacher) {
		this.tid = tid;
		this.teacher = teacher;
		this.setSpacing(20);
		this.setPadding(new Insets(20));

		// 1. Search Box
		VBox searchBox = new VBox(15);
		searchBox.setPadding(new Insets(25));
		searchBox.setStyle(
				"-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

		HBox row1 = new HBox(20);
		row1.setAlignment(Pos.CENTER_LEFT);
		row1.getChildren().addAll(new Label("学号"), sidField, new Label("学生名"), snameField, sFuzzyCheck);

		HBox row2 = new HBox(20);
		row2.setAlignment(Pos.CENTER_LEFT);
		row2.getChildren().addAll(new Label("课程号"), cidField, new Label("课程名"), cnameField, cFuzzyCheck);

		HBox row3 = new HBox(20);
		row3.setAlignment(Pos.CENTER_LEFT);
		termCombo.getItems().addAll("25-春季学期", "24-秋季学期"); // Example terms
		termCombo.getSelectionModel().select(0);
		Button resetBtn = new Button("重置");
		resetBtn.setStyle("-fx-background-color: #1890ff; -fx-text-fill: white;");
		resetBtn.setOnAction(e -> {
			sidField.clear();
			snameField.clear();
			sFuzzyCheck.setSelected(false);
			cidField.clear();
			cnameField.clear();
			cFuzzyCheck.setSelected(false);
			minGradeField.clear();
			maxGradeField.clear();
			loadData();
		});
		row3.getChildren().addAll(new Label("成绩下限"), minGradeField, new Label("成绩上限"), maxGradeField, new Label("选择学期"),
				termCombo);

		searchBox.getChildren().addAll(row1, row2, row3, resetBtn);

		// 2. Table
		createTable();

		// 3. Pagination
		pagination.setAlignment(Pos.CENTER_LEFT);

		this.getChildren().addAll(searchBox, table, pagination);

		// Listeners
		sidField.textProperty().addListener((o, old, n) -> loadData());
		snameField.textProperty().addListener((o, old, n) -> loadData());
		sFuzzyCheck.selectedProperty().addListener((o, old, n) -> loadData());
		cidField.textProperty().addListener((o, old, n) -> loadData());
		cnameField.textProperty().addListener((o, old, n) -> loadData());
		cFuzzyCheck.selectedProperty().addListener((o, old, n) -> loadData());
		minGradeField.textProperty().addListener((o, old, n) -> loadData());
		maxGradeField.textProperty().addListener((o, old, n) -> loadData());
		termCombo.setOnAction(e -> loadData());

		loadData();
	}

	private void createTable() {
		table.setPrefHeight(400);
		TableColumn<TeacherGradeData, Integer> cidCol = new TableColumn<>("课程号");
		cidCol.setCellValueFactory(new PropertyValueFactory<>("cid"));
		TableColumn<TeacherGradeData, Integer> sidCol = new TableColumn<>("学号");
		sidCol.setCellValueFactory(new PropertyValueFactory<>("sid"));
		TableColumn<TeacherGradeData, String> cnameCol = new TableColumn<>("课程名");
		cnameCol.setCellValueFactory(new PropertyValueFactory<>("cname"));
		TableColumn<TeacherGradeData, String> snameCol = new TableColumn<>("学生名");
		snameCol.setCellValueFactory(new PropertyValueFactory<>("sname"));
		TableColumn<TeacherGradeData, String> gradeCol = new TableColumn<>("成绩");
		gradeCol.setCellValueFactory(new PropertyValueFactory<>("gradeStr"));
		TableColumn<TeacherGradeData, String> termCol = new TableColumn<>("学期");
		termCol.setCellValueFactory(new PropertyValueFactory<>("term"));

		TableColumn<TeacherGradeData, Void> actionCol = new TableColumn<>("操作");
		actionCol.setCellFactory(col -> new TableCell<TeacherGradeData, Void>() {
			private final Button editBtn = new Button("编辑");
			{
				editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #1890ff; -fx-cursor: hand;");
				editBtn.setOnAction(e -> {
					TeacherGradeData data = getTableView().getItems().get(getIndex());
					teacher.showEditGrade(data);
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : editBtn);
			}
		});

		table.getColumns().addAll(cidCol, sidCol, cnameCol, snameCol, gradeCol, termCol, actionCol);
	}

	private void loadData() {
		allGrades = StuMysql.queryTeacherGrades(tid, sidField.getText(), snameField.getText(), sFuzzyCheck.isSelected(),
				cidField.getText(), cnameField.getText(), cFuzzyCheck.isSelected(),
				minGradeField.getText(), maxGradeField.getText(), termCombo.getValue());
		currentPage = 1;
		updatePage();
	}

	private void updatePage() {
		int start = (currentPage - 1) * pageSize;
		int end = Math.min(start + pageSize, allGrades.size());
		table.getItems().clear();
		if (start < allGrades.size())
			table.getItems().addAll(allGrades.subList(start, end));
		updatePagination();
	}

	private void updatePagination() {
		pagination.getChildren().clear();
		int totalPages = (int) Math.ceil((double) allGrades.size() / pageSize);
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
