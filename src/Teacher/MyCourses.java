package Teacher;

import java.util.List;

import Student.Course.CourseData;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import sql.StuMysql;

public class MyCourses extends VBox {
    private int tid;
    private TableView<CourseData> table = new TableView<>();

    public MyCourses(int tid) {
        this.tid = tid;
        this.setSpacing(20);
        this.setPadding(new Insets(20));

        Label title = new Label("我开设的课程");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        createTable();
        loadData();

        this.getChildren().addAll(title, table);
    }

    private void createTable() {
        TableColumn<CourseData, Integer> idCol = new TableColumn<>("课程号");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(150);

        TableColumn<CourseData, String> nameCol = new TableColumn<>("课程名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<CourseData, Integer> creditCol = new TableColumn<>("学分");
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));
        creditCol.setPrefWidth(150);

        TableColumn<CourseData, String> termCol = new TableColumn<>("学期");
        termCol.setCellValueFactory(new PropertyValueFactory<>("teacherName")); // stored term in teacherName
        termCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, nameCol, creditCol, termCol);
    }

    private void loadData() {
        List<CourseData> courses = StuMysql.queryTeacherCourses(tid);
        table.getItems().setAll(courses);
    }
}
