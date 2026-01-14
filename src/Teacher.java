package Teacher;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import sql.StuMysql;
import client.Login;

public class Teacher {
    private int tid;
    private String tname;
    private Stage stage;
    private VBox mainRoot = new VBox();
    private HBox topBar = new HBox();
    private HBox contentBox = new HBox();
    private VBox sidebar = new VBox();
    private StackPane displayArea = new StackPane();

    public Teacher(Stage stage, int tid) {
        this.stage = stage;
        this.tid = tid;
        this.tname = StuMysql.queryTname(tid);
        createView();
    }

    private void createView() {
        // 1. Top Bar
        topBar.getStyleClass().add("head");
        topBar.setSpacing(20);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10, 30, 10, 30));
        topBar.setStyle("-fx-background-color: #1890ff;");

        Button logoutBtn = new Button("logout");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> {
            Login login = new Login(stage);
            stage.setScene(new Scene(login.getView(), 350, 250));
        });

        Label termLabel = new Label("25-春季学期");
        termLabel.setStyle("-fx-text-fill: white;");

        Label userLabel = new Label(tname);
        userLabel.setStyle("-fx-text-fill: white;");

        topBar.getChildren().addAll(logoutBtn, new Label("|") {
            {
                setStyle("-fx-text-fill: white;");
            }
        }, termLabel, userLabel);

        // 2. Sidebar
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(20, 0, 0, 0));
        sidebar.setSpacing(5);
        sidebar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e9ecef; -fx-border-width: 0 1 0 0;");

        // Menu Items
        Label hiTeacher = new Label("  Hi! teacher");
        hiTeacher.setPrefWidth(200);
        hiTeacher.setPadding(new Insets(10));

        Button teacherEdit = createMenuButton("教师编辑");

        // Course Settings Dropdown
        TitledPane courseSettings = new TitledPane();
        courseSettings.setText("课程设置");
        courseSettings.setExpanded(true);
        VBox courseSubMenu = new VBox();
        Button myCourses = createMenuButton("  我开设的课程");
        Button openCourse = createMenuButton("  开设课程");
        courseSubMenu.getChildren().addAll(myCourses, openCourse);
        courseSettings.setContent(courseSubMenu);
        courseSettings.setStyle("-fx-background-color: transparent;");

        Button gradeManage = createMenuButton("教师成绩管理");

        sidebar.getChildren().addAll(hiTeacher, teacherEdit, courseSettings, gradeManage);

        // 3. Display Area
        displayArea.setPadding(new Insets(20));
        HBox.setHgrow(displayArea, Priority.ALWAYS);

        // Content
        contentBox.getChildren().addAll(sidebar, displayArea);
        VBox.setVgrow(contentBox, Priority.ALWAYS);

        mainRoot.getChildren().addAll(topBar, contentBox);

        // Actions
        myCourses.setOnAction(e -> {
            MyCourses mc = new MyCourses(tid);
            displayArea.getChildren().setAll(mc);
        });

        openCourse.setOnAction(e -> {
            OpenCourse oc = new OpenCourse(tid);
            displayArea.getChildren().setAll(oc);
        });

        // Default view
        OpenCourse oc = new OpenCourse(tid);
        displayArea.getChildren().setAll(oc);
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(10, 20, 10, 20));
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px;");
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #e6f7ff; -fx-text-fill: #1890ff; -fx-cursor: hand; -fx-font-size: 14px;"));
        btn.setOnMouseExited(
                e -> btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px;"));
        return btn;
    }

    public Parent getView() {
        return mainRoot;
    }
}
