package typingtrainer.StatisticScene;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import sun.plugin2.message.Message;
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
	private ArrayList<String> date_list = new ArrayList<String>();

	public void initialize()
	{
		System.out.println("Сцена статистики готова!");

		try
		{
			FileReader st_read = new FileReader("src/typingtrainer/StatisticScene/Statistics/statistic.txt");
			BufferedReader reader = new BufferedReader(st_read);
			int lastMistakes = 0, lastSpeed = 0, count = 0;
			double lastTime = 0.0, avgTime = 0.0, avgMistakes = 0.0, avgSpeed = 0.0;
			String buf, lastDate;
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
				buf = reader.readLine();
				lastDate = buf;
				date_list.add(lastDate);

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
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Файл статистики отсутствует или повреждён. Создать вместо него новый?", ButtonType.YES, ButtonType.NO);
			alert.setTitle("Ошибка");
			alert.setHeaderText(null);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES)
			{
				try
				{
					FileWriter writer = new FileWriter("src/typingtrainer/StatisticScene/Statistics/statistic.txt");
					writer.close();
				}
				catch (IOException e1)
				{
					new Alert(Alert.AlertType.ERROR, "Ошибка при создании файла.", ButtonType.OK).showAndWait();
				}
			}

			speed_list.clear();
			mistakes_list.clear();
			time_list.clear();

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

	public void onClearClicked(MouseEvent mouseEvent)
	{
		FileWriter st_write = null;
		try
		{
			st_write = new FileWriter("src/typingtrainer/StatisticScene/Statistics/statistic.txt");
			st_write.write("");
			st_write.flush();
			st_write.close();
		}
		catch (IOException e)
		{
			new Alert(Alert.AlertType.ERROR, "Ошибка при создании файла.", ButtonType.OK).showAndWait();
		}

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
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Сеансы");

		final LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);

		lineChart.setTitle("Статистика");

		ArrayList<XYChart.Series> datas = new ArrayList<>();

		XYChart.Series series = new XYChart.Series();
		series.setName("Время (мин)");

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("Ошибки (шт)");

		XYChart.Series series3 = new XYChart.Series();
		series3.setName("Скорость (зн/мин)");



		for (int i = 0; i < this.mistakes_list.size(); i++) {
			series.getData().add(new XYChart.Data((i + 1) + " (" + date_list.get(i) + ")", time_list.get(i)));
			series2.getData().add(new XYChart.Data((i+1) + " (" + date_list.get(i) + ")", mistakes_list.get(i)));
			series3.getData().add(new XYChart.Data((i+1) + " (" + date_list.get(i) + ")", speed_list.get(i)));
		}
		datas.add(series);
		datas.add(series2);
		datas.add(series3);

		sceneSetter(lineChart, datas);

	}

	public void onMistakesCliked(MouseEvent mouseEvent)
	{
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Сеансы");

		final LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);

		lineChart.setTitle("Статистика ошибок");

		XYChart.Series series = new XYChart.Series();
		series.setName("Ошибки (шт)");


		for (int i = 0; i < this.mistakes_list.size(); i++)
			series.getData().add(new XYChart.Data((i+1) + " (" + date_list.get(i) + ")", mistakes_list.get(i)));


		sceneSetter(lineChart, series);

	}


	public void onTimeCliked(MouseEvent mouseEvent)
	{
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Сеансы");

		final LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);

		lineChart.setTitle("Статистика времени");

		XYChart.Series series = new XYChart.Series();
		series.setName("Время (сек)");


		for (int i = 0; i < this.mistakes_list.size(); i++)
			series.getData().add(new XYChart.Data((i+1) + " (" + date_list.get(i) + ")", time_list.get(i)));

		sceneSetter(lineChart, series);
	}

	public void onSpeedCliked(MouseEvent mouseEvent)
	{
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Сеансы");

		final LineChart<String,Number> lineChart = new LineChart<String,Number>(xAxis,yAxis);

		lineChart.setTitle("Статистика скорости");

		XYChart.Series series = new XYChart.Series();
		series.setName("Скорость (зн/мин)");

		for (int i = 0; i < this.mistakes_list.size(); i++)
            series.getData().add(new XYChart.Data((i) + " (" + date_list.get(i) + ")", speed_list.get(i)));


		sceneSetter(lineChart, series);
	}


	private void sceneSetter(LineChart<String,Number> lineChart, XYChart.Series series)
	{


		final SceneManager sceneManager = ((ManagedScene)(pane.getScene())).getManager();
		lineChart.setOnMouseClicked(event ->
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

		ManagedScene graphScene = new ManagedScene(lineChart, 600, 600, sceneManager);
		lineChart.getData().add(series);
		sceneManager.pushScene(graphScene);
	}

	private void sceneSetter(LineChart<String,Number> lineChart, ArrayList<XYChart.Series> datas)
	{


		final SceneManager sceneManager = ((ManagedScene)(pane.getScene())).getManager();
		lineChart.setOnMouseClicked(event ->
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

		ManagedScene graphScene = new ManagedScene(lineChart, 600, 600, sceneManager);

		for(int i = 0; i < datas.size(); i++)
			lineChart.getData().add(datas.get(i));


		sceneManager.pushScene(graphScene);
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
