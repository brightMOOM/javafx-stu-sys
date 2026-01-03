package Student;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
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



public class Course extends VBox{
	 private TableView<CourseData> courseTable;
	    private List<CourseData> allCourses;
	    private int currentPage = 1;
	    private final int pageSize = 5;

	    public Course() {
	        // 1. 页面标题
	        Label titleLabel = new Label("学生课程首页");
	        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 0 0 15px 0;");

	        // 2. 初始化课程数据（与截图一致）
	        initCourseData();

	        // 3. 创建表格
	        createCourseTable();

	        // 4. 创建分页组件（与截图一致）
	        HBox pagination = createPagination();

	        // 5. 加载样式
	        StyleUtils.useCss(this, "/style.css");

	        // 6. 组装布局
	        this.setSpacing(10);
	        this.setPadding(new Insets(20));
	        this.getChildren().addAll(titleLabel, courseTable, pagination);
	    }

	    // 初始化截图中的课程数据
	    private void initCourseData() {
	        allCourses = StuMysql.queryCourse();
	    }

	    // 创建表格（绑定数据+样式）
	    private void createCourseTable() {
	        courseTable = new TableView<>();
	        courseTable.setPrefWidth(600);
	        courseTable.setPrefHeight(250);

	        TableColumn<CourseData, Integer> idCol = new TableColumn<>("课号");
	        idCol.setPrefWidth(120);
	        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

	        TableColumn<CourseData, String> nameCol = new TableColumn<>("课程名称");
	        nameCol.setPrefWidth(120);
	        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

	        TableColumn<CourseData, Integer> teacherIdCol = new TableColumn<>("教师号");
	        teacherIdCol.setPrefWidth(120);
	        teacherIdCol.setCellValueFactory(new PropertyValueFactory<>("teacherId"));

	        TableColumn<CourseData, String> teacherNameCol = new TableColumn<>("教师名称");
	        teacherNameCol.setPrefWidth(120);
	        teacherNameCol.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

	        TableColumn<CourseData, Void> actionCol = new TableColumn<>("操作");
	        actionCol.setPrefWidth(120);
	        actionCol.setCellFactory(new Callback<TableColumn<CourseData, Void>, TableCell<CourseData, Void>>() {
	            public TableCell<CourseData, Void> call(TableColumn<CourseData, Void> col) {
	                return new TableCell<CourseData, Void>() {
	                    private final Button selectBtn = new Button("选择");

	                    {
	                        // 绑定CSS样式类
	                        selectBtn.getStyleClass().add("btn-select");

	                        // 按钮点击事件
	                        selectBtn.setOnAction(event -> {
	                            CourseData selected = getTableView().getItems().get(getIndex());
	                            System.out.println("选择课程：" + selected.getName() + "（课号：" + selected.getId() + "）");
	                        });
	                    }

	                    @Override
	                    protected void updateItem(Void item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (empty) {
	                            setGraphic(null);
	                        } else {
	                            setGraphic(selectBtn);
	                        }
	                    }
	                };
	            }
	        });

	        // 添加列到表格
	        courseTable.getColumns().addAll(idCol, nameCol, teacherIdCol, teacherNameCol, actionCol);
	        // 加载第一页数据
	        loadPageData(currentPage);
	    }

	    // 创建分页组件（与截图一致）
	    private HBox createPagination() {
	        HBox pagination = new HBox(10);
	        pagination.setPadding(new Insets(10, 0, 0, 0));

	        // 上一页按钮
	        Button prevBtn = new Button("<");
	        prevBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #e9ecef; -fx-padding: 2px 8px;");
	        prevBtn.setOnAction(event -> {
	            if (currentPage > 1) {
	                currentPage--;
	                loadPageData(currentPage);
	                updatePaginationBtn(pagination, currentPage);
	            }
	        });

	        // 当前页码按钮（与截图一致：蓝色背景+白色文字）
	        Button pageBtn = new Button(String.valueOf(currentPage));
	        pageBtn.setStyle("-fx-background-color: #1890ff; -fx-text-fill: white; -fx-padding: 2px 8px;");

	        // 下一页按钮
	        Button nextBtn = new Button(">");
	        nextBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #e9ecef; -fx-padding: 2px 8px;");
	        nextBtn.setOnAction(event -> {
	            int totalPage = (int) Math.ceil((double) allCourses.size() / pageSize);
	            if (currentPage < totalPage) {
	                currentPage++;
	                loadPageData(currentPage);
	                updatePaginationBtn(pagination, currentPage);
	            }
	        });

	        pagination.getChildren().addAll(prevBtn, pageBtn, nextBtn);
	        return pagination;
	    }

	    // 更新分页页码
	    private void updatePaginationBtn(HBox pagination, int page) {
	        pagination.getChildren().set(1, new Button(String.valueOf(page)));
	        ((Button) pagination.getChildren().get(1)).setStyle("-fx-background-color: #1890ff; -fx-text-fill: white; -fx-padding: 2px 8px;");
	    }

	    // 加载分页数据
	    private void loadPageData(int page) {
	        int start = (page - 1) * pageSize;
	        int end = Math.min(start + pageSize, allCourses.size());
	        courseTable.getItems().clear();
	        courseTable.getItems().addAll(allCourses.subList(start, end));
	    }
	    //课程数据的内部类
	    public static class CourseData {
	        private int id;         // 课号
	        private String name;    // 课程号
	        private int teacherId;  // 教师号
	        private String teacherName; // 教师名称

	        public CourseData(int id, String name, int teacherId, String teacherName) {
	            this.id = id;
	            this.name = name;
	            this.teacherId = teacherId;
	            this.teacherName = teacherName;
	        }

	        // 保留public Getter方法
	        public int getId() { return id; }
	        public String getName() { return name; }
	        public int getTeacherId() { return teacherId; }
	        public String getTeacherName() { return teacherName; }
	    }
}
