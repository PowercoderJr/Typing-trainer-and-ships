package typingtrainer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import typingtrainer.InfoScene.InfoSceneController;

import java.io.IOException;

public class Main extends Application
{
	public static final int DEFAULT_SCREEN_WIDTH = 1280;
    public static final int DEFAULT_SCREEN_HEIGHT = 720;
    public static SceneManager sceneManager;

    public static void pushInfoScene(String message)
	{
		try
		{
			InfoSceneController.setInfo(message);
			Parent infoSceneFXML = FXMLLoader.load(Main.class.getResource("InfoScene/infoScene.fxml"));
			ManagedScene infoScene = new ManagedScene(infoSceneFXML, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT, sceneManager);
			infoScene.getStylesheets().add("typingtrainer/InfoScene/style.css");
			sceneManager.pushScene(infoScene);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

    @Override
    public void start(Stage primaryStage) throws Exception
	{
		sceneManager = new SceneManager(primaryStage);

        Parent mainSceneFXML = FXMLLoader.load(getClass().getResource("MainScene/mainScene.fxml"));
        ManagedScene mainScene = new ManagedScene(mainSceneFXML, DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT, sceneManager);
        mainScene.getStylesheets().add("typingtrainer/MainScene/style.css");

        sceneManager.pushScene(mainScene);
        primaryStage.setTitle("Typing trainer");
        primaryStage.setWidth(DEFAULT_SCREEN_WIDTH);
        primaryStage.setHeight(DEFAULT_SCREEN_HEIGHT);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Программу следует закрыть, нажав кнопку \"ВЫХОД\" в главном меню", ButtonType.OK);
                alert.setTitle("Привет, я - костыль!");
                alert.setHeaderText(null);
                alert.showAndWait();
                event.consume();
            }
        });
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}