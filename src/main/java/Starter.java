import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.HibernateUtil;

public class Starter extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {

        // ✅ Initialize Hibernate at startup
        HibernateUtil.getSessionFactory();

        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("view/login-form.fxml"))));
        stage.show();
        stage.setTitle("Clothify - Login");
    }
}
