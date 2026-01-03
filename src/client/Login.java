package client;

import Student.Student;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import sql.StuMysql;

public class Login {
    private GridPane root;
    private Stage stage;

    public Login(Stage stage) {
        this.stage = stage;
        createView();
    }

    private void createView() {
        Label title = new Label("用户登录");
        title.setFont(new Font(20));

        Label userLabel = new Label("用户名：");
        TextField userField = new TextField();

        Label passLabel = new Label("密码：");
        PasswordField passField = new PasswordField();

        Button loginBtn = new Button("登录");
        Label msgLabel = new Label();

        loginBtn.setOnAction(e -> {
        	int userid = Integer.parseInt(userField.getText());
            String pass = passField.getText();

            if (StuMysql.queryList(userid,pass)==true) {
                msgLabel.setText("登录成功");
                msgLabel.setStyle("-fx-text-fill: green;");
                // 切换到主界面
                Student student = new Student(stage,userid);
                stage.setScene(new Scene(student.getView(), 1000, 700));
                stage.setTitle("学生界面");
            } else {
                msgLabel.setText("用户名或密码错误");
                msgLabel.setStyle("-fx-text-fill: red;");
            }
        });

        root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setHgap(10);
        root.setVgap(15);
        root.setPadding(new Insets(20));

        root.add(title, 0, 0, 2, 1);
        root.add(userLabel, 0, 1);
        root.add(userField, 1, 1);
        root.add(passLabel, 0, 2);
        root.add(passField, 1, 2);
        root.add(loginBtn, 1, 3);
        root.add(msgLabel, 1, 4);
    }

    // 提供给 Main 使用
    public Parent getView() {
        return root;
    }
}
