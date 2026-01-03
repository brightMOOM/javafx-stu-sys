package client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // 创建登录界面
        Login login = new Login(stage);

        Scene scene = new Scene(login.getView(), 350, 250);
        stage.setTitle("登录系统");
        stage.setScene(scene);
        stage.setX(300);
        stage.setY(100);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
