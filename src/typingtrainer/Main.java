package typingtrainer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import typingtrainer.MainScene.ManagedScene;

public class Main extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
	{
		SceneManager sceneManager = new SceneManager(primaryStage);

        Parent mainSceneFXML = FXMLLoader.load(getClass().getResource("MainScene/mainScene.fxml"));
        ManagedScene mainScene = new ManagedScene(mainSceneFXML, 1280, 720, sceneManager);
        mainScene.getStylesheets().add("typingtrainer/MainScene/style.css");

        sceneManager.pushScene(mainScene);
        primaryStage.setTitle("Hello Bitch trainer");
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}