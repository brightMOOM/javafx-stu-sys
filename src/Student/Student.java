package Student;
import client.Login;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sql.StuMysql;
public class Student {
	private int sid;
	private String sname;
	private Stage stage;
	private Button logout=new Button("logout");
	private VBox vbox=new VBox();
	private HBox head=new HBox();
	private HBox hbox=new HBox();
    private HomeView homeview;
    private Course course=new Course();
    private Grade grade;
	ListView<String> listView = new ListView<>();
	public Student(Stage stage,int id) {
		this.stage = stage;
		this.sid=id;
        this.sname=StuMysql.querySname(this.sid);
        homeview=new HomeView(this.sid,this.sname);
        grade=new Grade(this.sid);
		createView();
	}
	private void createView() {    
		String cssPath = getClass().getResource("/style.css").toExternalForm();
        if (cssPath != null) {
            vbox.getStylesheets().add(cssPath); // 主布局加载，所有子控件继承
        } else {
            System.err.println("样式文件未找到！请检查路径：/style.css");
        }
        
		head.getStyleClass().add("head"); 
		listView.setPrefWidth(150);
		listView.setFixedCellSize(40);
		int visibleRows = 5;
		double cellHeight = 40;

		listView.setFixedCellSize(cellHeight);
		listView.setPrefHeight(cellHeight * visibleRows + 2);
        listView.getItems().addAll(
                "首页",
                "学生管理",
                "课程管理",
                "成绩管理",
                "退出系统"
        );
        
        vbox.getChildren().addAll(head,hbox);
        hbox.getChildren().add(listView);
		hbox.getChildren().add(homeview);
		head.getChildren().addAll(logout);
		
        ObservableList<Node> children = hbox.getChildren();
        listView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal == null) return;

                    switch (newVal) {
                        case "首页":
                        	if (!children.isEmpty()) {
                        	    children.remove(children.size() - 1);
                        	}
                            hbox.getChildren().add(homeview);
                            break;
                        case "学生管理":
                        	if (!children.isEmpty()) {
                        	    children.remove(children.size() - 1);
                        	}
                        	hbox.getChildren().add(course);
                            break;
                        case "课程管理":
                        	if (!children.isEmpty()) {
                        	    children.remove(children.size() - 1);
                        	}
                        	hbox.getChildren().add(course);
                            break;
                        case "成绩管理":
                        	if(!children.isEmpty()) {
                        	    children.remove(children.size() - 1);
                        	}
                            hbox.getChildren().add(grade);
                            break;
                        case "退出系统":
                        	Alert alert = new Alert(AlertType.CONFIRMATION);
                        	alert.setContentText("是否退出系统");
                        	alert.showAndWait().ifPresent(buttonType -> {
                                if (buttonType == ButtonType.OK) {
                                   System.exit(0);
                                } else if (buttonType == ButtonType.CANCEL) {
                                    
                                    
                                }
                            });
                            break;
                    }
                }
        );
        logout.setOnAction(e->{
        	Login login=new Login(this.stage);
        	this.stage.setScene(new Scene(login.getView(), 350, 250));
        }
        );

}
	public Parent getView() {
             return vbox;

}
	public int getSid() {
		return sid;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
}