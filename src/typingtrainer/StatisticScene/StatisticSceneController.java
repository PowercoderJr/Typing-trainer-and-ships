package typingtrainer.StatisticScene;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import typingtrainer.Main;
import typingtrainer.ManagedScene;
import typingtrainer.SceneManager;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


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

	private ArrayList<Integer> speed_list = new ArrayList<Integer>();
	private ArrayList<Integer> mistakes_list = new ArrayList<Integer>();
	private ArrayList<Double> time_list = new ArrayList<Double>();

    public void initialize() throws IOException
    {
        System.out.println("Сцена статистики готова!");




        try {
            FileReader st_read = new FileReader("src/typingtrainer/StatisticScene/Statistics/statistic.txt");
            BufferedReader reader = new BufferedReader(st_read);
            int lastMistakes = 0, lastSpeed = 0, count = 0;
            double lastTime = 0.0, avgTime = 0.0, avgMistakes = 0.0, avgSpeed = 0.0;
            String buf;
            //List<int> speed_list = new List<int>();


            while ((buf = reader.readLine()) != null) {
                lastMistakes = Integer.parseInt(buf);
                buf = reader.readLine();
                mistakes_list.add(lastMistakes);
                lastTime = Double.parseDouble(buf);
                buf = reader.readLine();
                time_list.add(lastTime);
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
            if (count > 0) {
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
        }catch (Exception e){
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

    public void onGraphClicked(MouseEvent mouseEvent) throws IOException {

        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();

        LineChart<Number, Number> numberLineChart = new LineChart<Number, Number>(x,y);
        numberLineChart.setTitle("Статистика");
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();

        series1.setName("Скорость(зн/мин)");
        series2.setName("Ошибки(шт)");
        series3.setName("Время(сек)");

        ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();
        ObservableList<XYChart.Data> datas2 = FXCollections.observableArrayList();
        ObservableList<XYChart.Data> datas3 = FXCollections.observableArrayList();

        for (int i = 0; i < this.speed_list.size();i++){
            datas.add(new XYChart.Data(i,this.speed_list.get(i)));
            datas2.add(new XYChart.Data(i,this.mistakes_list.get(i)));
            datas3.add(new XYChart.Data(i,this.time_list.get(i)));
        }

        series1.setData(datas);
        series2.setData(datas2);
        series3.setData(datas3);




        SceneManager sceneManager = ((ManagedScene)(((Label)mouseEvent.getSource()).getScene())).getManager();
       // Parent practiceSceneFXML = FXMLLoader.load(Main.class.getResource("ModScene/modScene.fxml"));
        ManagedScene practiceScene = new ManagedScene(numberLineChart, 600, 600, sceneManager);

        numberLineChart.getData().add(series1);
        numberLineChart.getData().add(series2);
        numberLineChart.getData().add(series3);
        sceneManager.pushScene(practiceScene);

    }
}
