package Teacher;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import sql.StuMysql;
import sql.StuMysql.TeacherGradeData;

public class EditGrade extends VBox {
    private int tid;
    private TeacherGradeData data;
    private TextField cnameField = new TextField();
    private TextField tnameField = new TextField();
    private TextField snameField = new TextField();
    private TextField gradeField = new TextField();

    public EditGrade(int tid, String tname, TeacherGradeData data) {
        this.tid = tid;
        this.data = data;
        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        this.setMaxWidth(600);
        this.setAlignment(Pos.TOP_LEFT);

        cnameField.setText(data.getCname()); cnameField.setEditable(false);
        tnameField.setText(tname); tnameField.setEditable(false);
        snameField.setText(data.getSname()); snameField.setEditable(false);
        gradeField.setText(data.getGrade() == -1.0f ? "" : String.valueOf(data.getGrade()));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        grid.add(new Label("课程名称"), 0, 0); grid.add(cnameField, 1, 0);
        grid.add(new Label("教师名"), 0, 1); grid.add(tnameField, 1, 1);
        grid.add(new Label("学生名"), 0, 2); grid.add(snameField, 1, 2);
        
        HBox gradeBox = new HBox(5);
        Label star = new Label("*"); star.setStyle("-fx-text-fill: red;");
        gradeBox.getChildren().addAll(star, new Label("分数"));
        grid.add(gradeBox, 0, 3); grid.add(gradeField, 1, 3);

        cnameField.setPrefWidth(400); tnameField.setPrefWidth(400);
        snameField.setPrefWidth(400); gradeField.setPrefWidth(400);

        HBox btnBox = new HBox(15);
        Button submitBtn = new Button("提交");
        submitBtn.setStyle("-fx-background-color: #1890ff; -fx-text-fill: white; -fx-padding: 8 25;");
        Button resetBtn = new Button("重置");
        resetBtn.setStyle("-fx-background-color: white; -fx-border-color: #d9d9d9; -fx-padding: 8 25;");
        Button testBtn = new Button("test");
        testBtn.setStyle("-fx-background-color: white; -fx-border-color: #d9d9d9; -fx-padding: 8 25;");

        submitBtn.setOnAction(e -> {
            try {
                float grade = Float.parseFloat(gradeField.getText());
                if (StuMysql.updateGrade(data.getSid(), data.getCid(), tid, data.getTerm(), grade)) {
                    new Alert(Alert.AlertType.INFORMATION, "成绩修改成功").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "成绩修改失败").showAndWait();
                }
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "请输入正确的分数").showAndWait();
            }
        });

        resetBtn.setOnAction(e -> gradeField.clear());

        btnBox.getChildren().addAll(submitBtn, resetBtn, testBtn);
        this.getChildren().addAll(grid, btnBox);
    }
}
