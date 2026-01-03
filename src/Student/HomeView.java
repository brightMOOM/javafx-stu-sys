package Student;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import stamet.StyleUtils;

public class HomeView extends VBox{
	
	public HomeView(int sid,String sname) {
		this.getChildren().add(new Label("学生首页"));
		this.getChildren().add(new Label("学生信息") {{this.getStyleClass().add("label-bold"); }});
		HBox hbox1=new HBox();
		VBox vbox1=new VBox();
		hbox1.getChildren().addAll(new Label("姓名： "+sname),new Label("       类型：学生"));
		vbox1.getChildren().add(hbox1);
		this.getChildren().add(vbox1);
		this.getStylesheets().add(HomeView.class.getResource("/style.css").toExternalForm());
		 vbox1.getStyleClass().add("info-card"); 
		 StyleUtils.useCss(this, "/style.css");
	}

}
