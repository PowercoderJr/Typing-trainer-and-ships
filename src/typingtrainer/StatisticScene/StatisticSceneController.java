package typingtrainer.StatisticScene;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import typingtrainer.ManagedScene;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;


/**
 * Created by Никитка on 12.03.2017.
 */
public class StatisticSceneController {

	@FXML
    public Label labelMisAll;
	@FXML
    public Label labelMisLast;
	@FXML
    public Label labelTimeAll;
	@FXML
    public Label labelTimeLast;
	@FXML
    public Label labelSpeedAll;
	@FXML
    public Label labelSpeedLast;
	@FXML
	public Label labelAvgHeader;

    public void initialize() throws IOException
    {
        System.out.println("Сцена статистики готова!");

        FileReader st_read = new FileReader("src/typingtrainer/StatisticScene/Statistics/statistic.txt");
        BufferedReader reader = new BufferedReader(st_read);
        int lastMistakes = 0, lastSpeed = 0, count = 0;
        double lastTime = 0.0, avgTime = 0.0, avgMistakes = 0.0, avgSpeed = 0.0;
        String buf;
        while ((buf = reader.readLine()) != null)
        {
        	lastMistakes = Integer.parseInt(buf);
			buf = reader.readLine();
			lastTime = Double.parseDouble(buf);
			buf = reader.readLine();
			lastSpeed = Integer.parseInt(buf);

			avgMistakes += lastMistakes;
			avgTime += lastTime;
			avgSpeed += lastSpeed;

			++count;
        }
		reader.close();
		/** Не нужно ли также закрывать и st_read или закрывать ТОЛЬКО его? Я хз как на самом деле, но открываем мы именно его, а не reader **/

		//Общая
		if (count > 0)
		{
			avgMistakes /= 1.0 * count;
			avgTime /= 1.0 * count;
			avgSpeed /= 1.0 * count;
		}
		labelAvgHeader.setText("Среднее за " + count + (count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 12 || count % 100 > 14) ? " раза" : " раз"));
		labelMisAll.setText(String.format("%.2f", avgMistakes));
		labelTimeAll.setText(String.format("%.2f", avgTime) + " сек");
		labelSpeedAll.setText(String.format("%.2f", avgSpeed) + " зн/мин");

		//Последний сеанс
		labelMisLast.setText("" + lastMistakes);
		labelTimeLast.setText(String.format("%.2f", lastTime) + " сек");
		labelSpeedLast.setText(lastSpeed + " зн/мин");
    }

    public void onBackClicked(MouseEvent mouseEvent)
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

    public void onClearClicked(MouseEvent mouseEvent) throws IOException {
        FileWriter st_write = new FileWriter("src/typingtrainer/StatisticScene/Statistics/statistic.txt");
        st_write.write("");
        st_write.flush();
        st_write.close();

        //Общая
		labelAvgHeader.setText("Среднее за 0 раз");
        labelMisAll.setText("0,00");
        labelTimeAll.setText("0,00 сек");
        labelSpeedAll.setText("0,00 зн/мни");

        //Последний сеанс
        labelMisLast.setText("0");
        labelTimeLast.setText("0,00 сек");
        labelSpeedLast.setText("0 зн/мин");
    }
}
