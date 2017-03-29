package typingtrainer.StatisticScene;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import typingtrainer.ManagedScene;
import typingtrainer.SceneManager;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;


/**
 * Created by Никитка on 12.03.2017.
 */
public class StatisticSceneController
{

	@FXML
	public GridPane pane;
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

	private ArrayList<Integer> speed_list = new ArrayList<Integer>();
	private ArrayList<Integer> mistakes_list = new ArrayList<Integer>();
	private ArrayList<Double> time_list = new ArrayList<Double>();

	public void initialize()
	{
		System.out.println("Сцена статистики готова!");


		try
		{
			FileReader st_read = new FileReader("src/typingtrainer/StatisticScene/Statistics/statistic.txt");
			BufferedReader reader = new BufferedReader(st_read);
			int lastMistakes = 0, lastSpeed = 0, count = 0;
			double lastTime = 0.0, avgTime = 0.0, avgMistakes = 0.0, avgSpeed = 0.0;
			String buf;
			//List<int> speed_list = new List<int>();

			while ((buf = reader.readLine()) != null)
			{
				lastMistakes = Integer.parseInt(buf);
				mistakes_list.add(lastMistakes);
				buf = reader.readLine();
				lastTime = Double.parseDouble(buf);
				time_list.add(lastTime);
				buf = reader.readLine();
				lastSpeed = Integer.parseInt(buf);
				speed_list.add(lastSpeed);

				avgMistakes += lastMistakes;
				avgTime += lastTime;
				avgSpeed += lastSpeed;

				++count;
			}
			reader.close();
			st_read.close();

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
		catch (Exception e)
		{
			System.out.println("Файл статистики отсутствует");
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Ошибка");
			alert.setHeaderText(null);
			alert.setContentText("Файл статистики отсутствует");
			alert.showAndWait();

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

	public void onClearClicked(MouseEvent mouseEvent) throws IOException
	{
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

	public void onGraphClicked(MouseEvent mouseEvent)
	{
		ArrayList<ObservableList<XYChart.Data>> datas = new ArrayList<>();
		ObservableList<XYChart.Data> data1 = FXCollections.observableArrayList();
		ObservableList<XYChart.Data> data2 = FXCollections.observableArrayList();
		ObservableList<XYChart.Data> data3 = FXCollections.observableArrayList();
		for (int i = 0; i < this.speed_list.size(); i++)
		{
			data1.add(new XYChart.Data(i, this.speed_list.get(i)));
			data2.add(new XYChart.Data(i, this.mistakes_list.get(i)));
			data3.add(new XYChart.Data(i, this.time_list.get(i)));
		}
		datas.add(data1);
		datas.add(data2);
		datas.add(data3);

		ArrayList<String> seriesNames = new ArrayList<>();
		seriesNames.add("Скорость (зн/мин)");
		seriesNames.add("Ошибки (шт)");
		seriesNames.add("Время (сек)");

		buildGraphScene("Статистика", seriesNames, datas);
	}

	public void onMistakesCliked(MouseEvent mouseEvent)
	{
		ArrayList<ObservableList<XYChart.Data>> datas = new ArrayList<>();
		ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
		for (int i = 0; i < this.mistakes_list.size(); i++) { data.add(new XYChart.Data(i, this.mistakes_list.get(i))); }
		datas.add(data);

		ArrayList<String> seriesNames = new ArrayList<>();
		seriesNames.add("Ошибки (шт)");

		buildGraphScene("Статистика ошибок", seriesNames, datas);
	}


	public void onTimeCliked(MouseEvent mouseEvent)
	{
		ArrayList<ObservableList<XYChart.Data>> datas = new ArrayList<>();
		ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
		for (int i = 0; i < this.time_list.size(); i++) { data.add(new XYChart.Data(i, this.time_list.get(i))); }
		datas.add(data);

		ArrayList<String> seriesNames = new ArrayList<>();
		seriesNames.add("Время (сек)");

		buildGraphScene("Статистика времени", seriesNames, datas);
	}

	public void onSpeedCliked(MouseEvent mouseEvent)
	{
		ArrayList<ObservableList<XYChart.Data>> datas = new ArrayList<>();
		ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
		for (int i = 0; i < this.speed_list.size(); i++) { data.add(new XYChart.Data(i, this.speed_list.get(i))); }
		datas.add(data);

		ArrayList<String> seriesNames = new ArrayList<>();
		seriesNames.add("Скорость (зн/мин)");

		buildGraphScene("Статистика скорости", seriesNames, datas);
	}

	private void buildGraphScene(String chartTitle, ArrayList<String> seriesNames, ArrayList<ObservableList<XYChart.Data>> datas)
	{
		LineChart<Number, Number> numberLineChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
		numberLineChart.setTitle(chartTitle);
		XYChart.Series series;
		for (int i = 0; i < datas.size(); ++i)
		{
			series = new XYChart.Series();
			if (i < seriesNames.size())
				series.setName(seriesNames.get(i));
			series.setData(datas.get(i));
			numberLineChart.getData().add(series);
		}

		final SceneManager sceneManager = ((ManagedScene)(pane.getScene())).getManager();
		numberLineChart.setOnMouseClicked(event ->
		{
			try
			{
				sceneManager.popScene();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
		});

		ManagedScene graphScene = new ManagedScene(numberLineChart, 600, 600, sceneManager);
		sceneManager.pushScene(graphScene);
	}
}
