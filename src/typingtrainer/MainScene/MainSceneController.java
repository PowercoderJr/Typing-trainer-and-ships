package typingtrainer.MainScene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import typingtrainer.Main;
import typingtrainer.SceneManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class MainSceneController
{
	public void initialize()
	{
		System.out.println("Главная сцена готова!");
	}

	public void onLabelClicked(MouseEvent mouseEvent)
	{
		Label lbl = (Label)mouseEvent.getSource();
		lbl.setText("WAZZZZZUUUUUP!!!");
	}

	public void onPracticeModeLabelClicked(MouseEvent mouseEvent) throws IOException
	{
		SceneManager sceneManager = ((ManagedScene)(((Label)mouseEvent.getSource()).getScene())).getManager();
		Parent practiceSceneFXML = FXMLLoader.load(Main.class.getResource("PracticeScene/practiceScene.fxml"));
		ManagedScene practiceScene = new ManagedScene(practiceSceneFXML, 1280, 720, sceneManager);
		practiceScene.getStylesheets().add("typingtrainer/PracticeScene/style.css");
		sceneManager.pushScene(practiceScene);
	}

	public void onExitLabelClicked(MouseEvent mouseEvent)
	{
		try
		{
			((ManagedScene)(((Label)mouseEvent.getSource()).getScene())).getManager().popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage() + " Suka!");
		}
	}
}