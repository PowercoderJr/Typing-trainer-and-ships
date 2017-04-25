package typingtrainer.CongScene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import typingtrainer.Main;
import typingtrainer.ManagedScene;
import typingtrainer.SceneManager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Никитка on 28.02.2017.
 */
public class CongSceneController
{

	@FXML
	public Label label_speed;
	public Label label_mistakes;
	public Label label_time;



	static int mistakes;
	static int speed;
	static double time;


	public void initialize()
	{
		System.out.println("Сцена окончания сеанса готова!");

		label_time.setText(String.format("%.2f", time) + "сек.");
		label_mistakes.setText( String.valueOf(mistakes) + "шт.");
		label_speed.setText( String.valueOf(speed) + "зн/мин");
	}

	public void onGoClicked(MouseEvent mouseEvent) throws IOException
	{

		SceneManager sceneManager = ((ManagedScene)(((Label)mouseEvent.getSource()).getScene())).getManager();
		Parent practiceSceneFXML = FXMLLoader.load(Main.class.getResource("ModScene/modScene.fxml"));
		ManagedScene practiceScene = new ManagedScene(practiceSceneFXML, 1280, 720, sceneManager);
		practiceScene.getStylesheets().add("typingtrainer/ModScene/style.css");
		sceneManager.pushScene(practiceScene);
	}

	public void onBackClicked(MouseEvent mouseEvent)
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

	public static void SetResaults(int n_speed, double n_time, int n_mistakes){
		speed = n_speed;
		time = n_time;
		mistakes = n_mistakes;
	}
}
