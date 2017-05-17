package typingtrainer.InfoScene;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import typingtrainer.Main;
import typingtrainer.ManagedScene;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Meow on 13.05.2017.
 */
public class InfoSceneController
{
	@FXML
	public Label infoLabel;
	@FXML
	public Label backLabel;

	private static String info;

	public void initialize()
	{
		System.out.println("Сцена с информацией готова!");
		infoLabel.setText(info);
	}

	public void onBackClicked(MouseEvent mouseEvent) throws IOException
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

	public static void setInfo(String info)
	{
		InfoSceneController.info = info;
	}
}
