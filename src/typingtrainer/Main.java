package typingtrainer;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
        primaryStage.setTitle("Typing trainer");
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
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