package typingtrainer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    private Scene mainScene;
    private Scene practiceScene;
    private Scene pvpScene;

    @Override
    public void start(Stage primaryStage) throws Exception
	{
        Parent mainSceneFXML = FXMLLoader.load(getClass().getResource("MainScene/mainScene.fxml"));
        mainScene = new Scene(mainSceneFXML, 1280, 720);
        mainScene.getStylesheets().add("typingtrainer/MainScene/style.css");

        Parent trainigSceneFXML = FXMLLoader.load(getClass().getResource("PracticeScene/practiceScene.fxml"));
        practiceScene = new Scene(trainigSceneFXML, 1280, 720);
        practiceScene.getStylesheets().add("typingtrainer/PracticeScene/style.css");

        primaryStage.setTitle("Hello Bitch trainer");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}