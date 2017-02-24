package typingtrainer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
	{
        Parent root = FXMLLoader.load(getClass().getResource("MainScene/mainScene.fxml"));
        primaryStage.setTitle("Hello Bitch");
        Scene mainscene = new Scene(root, 1280, 720);
        mainscene.getStylesheets().add("typingtrainer/MainScene/style.css");
        primaryStage.setScene(mainscene);
        primaryStage.show();
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
