package client;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Student {
	private Stage stage;
	private HBox hbox=new HBox();
	public Student(Stage stage) {
		this.stage = stage;
		createView();
	}
	private void createView() {
        Label title = new Label("学生界面");
        title.setFont(new Font(20));
        hbox.getChildren().add(title);        

}
	public Parent getView() {
             return hbox;

}
}