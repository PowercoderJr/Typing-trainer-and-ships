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
	@FXML
	public ImageView logoImg;

	public void initialize() throws IOException
	{
		System.out.println("Главная сцена готова!");
	}

	public void onLabelClicked(MouseEvent mouseEvent)
	{
		Label lbl = (Label) mouseEvent.getSource();
		lbl.setText("WAZZZZZUUUUUP!!!");
	}

	public void onPracticeModeLabelClicked(MouseEvent mouseEvent) throws IOException
	{
		SceneManager sceneManager = ((ManagedScene) (((Label) mouseEvent.getSource()).getScene())).getManager();
		Parent practiceSceneFXML = FXMLLoader.load(Main.class.getResource("ModScene/modScene.fxml"));
		ManagedScene practiceScene = new ManagedScene(practiceSceneFXML, 1280, 720, sceneManager);
		practiceScene.getStylesheets().add("typingtrainer/ModScene/style.css");
		sceneManager.pushScene(practiceScene);
	}

	public void onExitLabelClicked(MouseEvent mouseEvent)
	{
		try
		{
			((ManagedScene) (((Label) mouseEvent.getSource()).getScene())).getManager().popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public void onStatLableClicked(MouseEvent mouseEvent) throws IOException
	{
		SceneManager sceneManager = ((ManagedScene) (((Label) mouseEvent.getSource()).getScene())).getManager();
		Parent practiceSceneFXML = FXMLLoader.load(Main.class.getResource("StatisticScene/statisticScene.fxml"));
		ManagedScene practiceScene = new ManagedScene(practiceSceneFXML, 1280, 720, sceneManager);
		practiceScene.getStylesheets().add("typingtrainer/statisticScene/style.css");
		sceneManager.pushScene(practiceScene);
	}

	public void onLobbyClicked(MouseEvent mouseEvent) throws IOException
	{
		SceneManager sceneManager = ((ManagedScene) (((Label) mouseEvent.getSource()).getScene())).getManager();
		Parent lobbySceneFXML = FXMLLoader.load(Main.class.getResource("LobbyScene/lobbyScene.fxml"));
		ManagedScene lobbyScene = new ManagedScene(lobbySceneFXML, 1280, 720, sceneManager);
		lobbyScene.getStylesheets().add("typingtrainer/LobbyScene/style.css");
		sceneManager.pushScene(lobbyScene);
	}
}