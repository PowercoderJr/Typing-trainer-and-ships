package typingtrainer.MainScene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import typingtrainer.Main;
import typingtrainer.ManagedScene;
import typingtrainer.SceneManager;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MainSceneController
{
	public static final Image BUTTONS_SPRITESHEET = new Image("typingtrainer/buttons_spritesheet.png");

	@FXML
	public ImageView logoImg;

	public void initialize() throws IOException
	{
		System.out.println("Главная сцена готова!");
	}

	public void onPracticeModeLabelClicked(MouseEvent mouseEvent) throws IOException
	{
		Parent practiceSceneFXML = FXMLLoader.load(Main.class.getResource("ModScene/modScene.fxml"));
		ManagedScene practiceScene = new ManagedScene(practiceSceneFXML, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT, Main.sceneManager);
		practiceScene.getStylesheets().add("typingtrainer/ModScene/style.css");
		Main.sceneManager.pushScene(practiceScene);
	}

	public void onExitLabelClicked(MouseEvent mouseEvent)
	{
		try
		{
			Main.sceneManager.popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public void onStatLableClicked(MouseEvent mouseEvent) throws IOException
	{
		Parent statisticSceneFXML = FXMLLoader.load(Main.class.getResource("StatisticScene/statisticScene.fxml"));
		ManagedScene statisticScene = new ManagedScene(statisticSceneFXML, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT, Main.sceneManager);
		statisticScene.getStylesheets().add("typingtrainer/StatisticScene/style.css");
		Main.sceneManager.pushScene(statisticScene);
	}

	public void onLobbyClicked(MouseEvent mouseEvent) throws IOException
	{
		Parent lobbySceneFXML = FXMLLoader.load(Main.class.getResource("LobbyScene/lobbyScene.fxml"));
		ManagedScene lobbyScene = new ManagedScene(lobbySceneFXML, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT, Main.sceneManager);
		lobbyScene.getStylesheets().add("typingtrainer/LobbyScene/style.css");
		Main.sceneManager.pushScene(lobbyScene);
	}

	public void onAuthorsClicked(MouseEvent mouseEvent)
	{
		Main.pushInfoScene("Авторы:\n\nКомаричев Р.Е.\nПерминов Н.Д.\n\n© 2017");
	}
}