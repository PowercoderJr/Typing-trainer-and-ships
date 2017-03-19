package typingtrainer.StatisticScene;


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

    public Label labelMisAll;
    public Label labelMisLast;
    public Label labelTimeAll;
    public Label labelTimeLast;
    public Label labelSpeedAll;
    public Label labelSpeedLast;

    public void initialize() throws IOException
    {
        System.out.println("Сцена статистики готова!");

        FileReader st_read = new FileReader("src/typingtrainer/StatisticScene/Statistics/statistic.txt");
        BufferedReader reader = new BufferedReader(st_read);
        String ln;
        ArrayList<String> lns = new ArrayList<String>();
        while ((ln = reader.readLine())!=null){
            lns.add(ln);
        }

        if (!lns.isEmpty()) {
            double mistakes = 0, time = 0, speed = 0;
            for (int i = 0; i <= lns.size() - 3; i += 3) {
                mistakes += Double.valueOf(lns.get(i));
                time += Double.valueOf(lns.get(i + 1));
                speed += Double.valueOf(lns.get(i + 2));
            }
            mistakes /= lns.size() / 3;
            time /= lns.size() / 3;
            speed /= lns.size() / 3;

            //Общая
            labelMisAll.setText(String.format("%.2f", mistakes));
            labelTimeAll.setText(String.format("%.2f", time) + " сек");
            labelSpeedAll.setText(String.format("%.2f", speed) + " зн/мин");

            //Последний сеанс
            labelMisLast.setText(lns.get(lns.size() - 3));
            labelTimeLast.setText(String.format("%.2f", Double.valueOf(lns.get(lns.size() - 2))) + " сек");
            labelSpeedLast.setText(lns.get(lns.size() - 1) + " зн/мин");

            reader.close();
        }else {
            //Общая
            labelMisAll.setText(String.valueOf(0));
            labelTimeAll.setText(String.valueOf(0));
            labelSpeedAll.setText(String.valueOf(0));

            //Последний сеанс
            labelMisLast.setText(String.valueOf(0));
            labelTimeLast.setText(String.valueOf(0));
            labelSpeedLast.setText(String.valueOf(0));
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
        labelMisAll.setText(String.valueOf(0));
        labelTimeAll.setText(String.valueOf(0));
        labelSpeedAll.setText(String.valueOf(0));

        //Последний сеанс
        labelMisLast.setText(String.valueOf(0));
        labelTimeLast.setText(String.valueOf(0));
        labelSpeedLast.setText(String.valueOf(0));
    }
}
