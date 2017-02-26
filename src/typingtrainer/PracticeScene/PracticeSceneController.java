package typingtrainer.PracticeScene;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import typingtrainer.ManagedScene;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Meow on 25.02.2017.
 */
public class PracticeSceneController
{
	public void initialize()
	{
		System.out.println("Сцена практики готова!");
	}


	public void onBackLabelClicked(MouseEvent mouseEvent)
	{
		try
		{
			((ManagedScene)(((Label)mouseEvent.getSource()).getScene())).getManager().popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
