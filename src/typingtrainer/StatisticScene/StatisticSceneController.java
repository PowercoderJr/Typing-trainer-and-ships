package typingtrainer.StatisticScene;


import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import typingtrainer.ManagedScene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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


    public void initialize() throws IOException {



        FileReader all_stat = new FileReader("src/typingtrainer/MainScene/Statistics/all_stat.txt");
        BufferedReader reader = new BufferedReader(all_stat);
        String line;
        ArrayList<String> lines = new ArrayList<String>();
        while ((line = reader.readLine())!=null){
            lines.add(line);
        }

        for (int i = 0; i < lines.size();i++)
            System.out.println(lines.get(i));
        reader.close();

        all_stat.close();

        labelMisAll.setText(String.format("%.2f",Double.valueOf(lines.get(0))));
        labelTimeAll.setText(String.format("%.2f",Double.valueOf(lines.get(1)))+" сек");
        labelSpeedAll.setText(String.format("%.2f",Double.valueOf(lines.get(2)))+" зн/мин");


        FileReader last_stat = new FileReader("src/typingtrainer/MainScene/Statistics/last_stat.txt");
        BufferedReader reader_last = new BufferedReader(last_stat);
        String line_last;
        ArrayList<String> lines_last = new ArrayList<String>();
        while ((line_last = reader_last.readLine())!=null){
            lines_last.add(line_last);
        }

        for (int i = 0; i < lines_last.size();i++)
            System.out.println(lines_last.get(i));
        reader.close();

        last_stat.close();

        labelMisLast.setText(lines_last.get(0));
        labelTimeLast.setText(lines_last.get(1));
        labelSpeedLast.setText(lines_last.get(2));

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
}
