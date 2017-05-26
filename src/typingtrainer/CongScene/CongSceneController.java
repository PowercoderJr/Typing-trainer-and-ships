package typingtrainer.CongScene;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import typingtrainer.Main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Никитка on 28.02.2017.
 */
public class CongSceneController
{
	@FXML
	public Label label_speed;
	@FXML
	public Label label_mistakes;
	@FXML
	public Label label_time;
	@FXML
	public Label label_statisticsStatus;

	private static int mistakes;
	private static int speed;
	private static double time;
	private static boolean isStatisticsSaved;

	public void initialize()
	{
		System.out.println("Сцена окончания сеанса готова!");

		label_time.setText(String.format("%.2f", time) + " сек");
		label_mistakes.setText( String.valueOf(mistakes) + " шт.");
		label_speed.setText( String.valueOf(speed) + " зн/мин");
		label_statisticsStatus.setText(isStatisticsSaved ? "Статистика успешно записана" : "При записи статистики произошла ошибка");
	}

	public void onNextClicked(MouseEvent mouseEvent) throws IOException
	{
		try
		{
			Main.sceneManager.popScene();
			Main.sceneManager.popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public void onMenuClicked(MouseEvent mouseEvent)
	{
		try
		{
			Main.sceneManager.popAllExceptFirst();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void SetResults(int n_speed, double n_time, int n_mistakes, boolean n_isStatisticsSaved){
		speed = n_speed;
		time = n_time;
		mistakes = n_mistakes;
		isStatisticsSaved = n_isStatisticsSaved;
	}
}
