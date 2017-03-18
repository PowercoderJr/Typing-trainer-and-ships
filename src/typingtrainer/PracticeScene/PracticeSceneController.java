package typingtrainer.PracticeScene;

import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import typingtrainer.ManagedScene;
import typingtrainer.PracticeWatcher;
import typingtrainer.Word;

import java.awt.*;
import java.awt.List;
import java.awt.im.InputContext;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Meow on 25.02.2017.
 */
public class PracticeSceneController
{
	@FXML
	public GridPane pane;
	@FXML
	public Label mainMenuLabel;
	@FXML
	public Label displayableStringLabel;
	@FXML
	public Rectangle highlightRct;
	@FXML
	public Rectangle highlightLShiftRct;
	@FXML
	public Rectangle highlightRShiftRct;
	@FXML
	private Rectangle highlightSpaceRct;

	private static final int VOLUME_REDUCING_DELAY = 2000;
	private static final int VOLUME_REDUCING_STEP = 50;
	private static final int SECONDS_MINUTE_CONTAIN = 60;
	private static final double SECONDS_NANOSECOND_CONTAIN = 1e-9;

	private PracticeWatcher watcher;
	private Object[] sceneParams;
	private volatile MediaPlayer music;
	private volatile MediaPlayer falseNote;
	private volatile int msToReducing;

	static Word.Languages lang;
	static int difficulty;
	static boolean register;
	private static int[][] keyCoordinates = {
			{396, 180},	//а
			{649, 180},	//о
			{311, 180},	//в
			{733, 180},	//л
			{227, 180},	//ы
			{817, 180},	//д
			{143, 180},	//ф
			{901, 180},	//ж
			{480, 180},	//п
			{564, 180},	//р
			{370, 93},	//к
			{623, 93},	//г
			{455, 93},	//е
			{539, 93},	//н
			{426, 268},	//м
			{682, 268},	//ь
			{514, 268},	//и
			{598, 268},	//т
			{286, 93},	//у
			{708, 93},	//ш
			{345, 268},	//с
			{767, 268},	//б
			{202, 93},	//ц
			{792, 93},	//щ
			{261, 268},	//ч
			{851, 268},	//ю
			{117, 93},	//й
			{876, 93},	//з
			{176, 268},	//я
			{936, 268},	//.
			{961, 93},	//х
			{1045, 93},	//ъ
			{986, 180},	//э
	};

	public void initialize()
	{
		System.out.println("Сцена практики готова!");
		restart();
		InputContext InCon = java.awt.im.InputContext.getInstance();
		InCon.selectInputMethod(new Locale("en", "US"));
	}

	private void restart()
	{
		disposeSounds();
		/*Временный фикс*/
		StringBuffer taskWord = new StringBuffer(Word.generateRndWord((int)(1 + Math.random() * 15),
				PracticeSceneController.difficulty,	PracticeSceneController.lang, PracticeSceneController.register));
		while (taskWord.length() < 200)
			taskWord.append(" " + Word.generateRndWord((int)(1 + Math.random() * 15), PracticeSceneController.difficulty,
					PracticeSceneController.lang, PracticeSceneController.register));

		/*StringBuffer taskWord = new StringBuffer(Word.generateRndWord(20,
				PracticeSceneController.difficulty,	PracticeSceneController.lang, PracticeSceneController.register));*/

		watcher = new PracticeWatcher(taskWord, PracticeSceneController.lang, PracticeSceneController.difficulty, PracticeSceneController.register);
		updHighlights();
		displayableStringLabel.setText(watcher.getDisplayableString());
		music = new MediaPlayer(new Media(new File("src/typingtrainer/PracticeScene/music/practice_" + (int)(1 + Math.random() * 6) + ".mp3").toURI().toString()));
		msToReducing = 0;
	}

	public void onMainMenuLabelClicked(MouseEvent mouseEvent)
	{
		disposeSounds();
		try
		{
			((ManagedScene)(((Label)mouseEvent.getSource()).getScene())).getManager().popAllExceptFirst();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public void onKeyPressed(KeyEvent keyEvent) throws IOException {
		if (!keyEvent.getCode().toString().equals("CONTROL") &&
				!keyEvent.getCode().toString().equals("SHIFT") &&
				!keyEvent.getCode().toString().equals("ALT") &&
				!keyEvent.getCode().toString().equals("ALT_GRAPH"))
		{
			boolean isSymbolCorrect;
			//System.out.println(keyEvent.getText().charAt(0));
			if (!keyEvent.getText().isEmpty())
			{
				if (watcher.getCurrentChar() == ' ')
				{
					isSymbolCorrect = keyEvent.getText().charAt(0) == ' ';
				}
				else
				{
					if (keyEvent.isShiftDown())
					{
						char symbolWithShift;
						String[] alphabet;
						switch (watcher.getLang())
						{
							case RU:
							default:
								alphabet = Word.ALPH_RU;
								break;
							case EN:
								alphabet = Word.ALPH_EN;
								break;
						}
						int symbolIndex = alphabet[0].indexOf(keyEvent.getText().charAt(0));
						if (symbolIndex >= 0 && symbolIndex < Word.MAX_LEVEL)
						{
							symbolWithShift = alphabet[1].charAt(symbolIndex);
							isSymbolCorrect = symbolWithShift == watcher.getCurrentChar();
						}
						else
						{
							isSymbolCorrect = false;
						}
					}
					else
					{
						isSymbolCorrect = keyEvent.getText().charAt(0) == watcher.getCurrentChar();
					}
				}
			}
			else
				isSymbolCorrect = false;

			if (isSymbolCorrect)
			{
				playGoodMusic();
				watcher.passCurrentChar();
				if (watcher.getDisplayableString().length() != 0)
				{
					displayableStringLabel.setText(watcher.getDisplayableString());
					updHighlights();
				}
				else
				{
					disposeSounds();
					//Тут будет сцена со статистикой
					try
					{
						((ManagedScene)(displayableStringLabel.getScene())).getManager().popAllExceptFirst();
					}
					catch (InvocationTargetException e)
					{
						System.out.println(e.getMessage());
					}

					displayableStringLabel.setText("");
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Поздравляем");
					alert.setHeaderText(null);

					int mistakes = watcher.getMistakeCount();
					double time = watcher.getFinalTime() * PracticeSceneController.SECONDS_NANOSECOND_CONTAIN;
					int speed = (int)(watcher.getInitStringLength() * PracticeSceneController.SECONDS_MINUTE_CONTAIN / time);

					alert.setContentText("Ошибки: " + mistakes + "\r\nВремя: " + String.format("%.2f", time) +
							" секунд\r\nСкорость: " + speed + " зн/мин");
					alert.showAndWait();

					FileWriter statistics = new FileWriter("src/typingtrainer/StatisticScene/Statistics/last_stat.txt");
					Date curr_date = new Date();
					statistics.write(mistakes + "\r\n" + String.format("%.2f", time) + "\r\n" +	speed + "\r\n"
							+ curr_date.toString());
					statistics.flush();

					FileReader all_stat = new FileReader("src/typingtrainer/StatisticScene/Statistics/all_stat.txt");
					BufferedReader reader = new BufferedReader(all_stat);
					String line;
					ArrayList<String> lines = new ArrayList<String>();
					while ((line = reader.readLine())!=null){
						lines.add(line);
					}

					/* Отладочная часть. Ещё понадобится
					for (int i = 0; i < lines.size();i++)
						System.out.println(lines.get(i));
					reader.close();
					*/
					all_stat.close();



					FileWriter new_stat = new FileWriter("src/typingtrainer/StatisticScene/Statistics/all_stat.txt");
					String new_st;
					if (lines.isEmpty()){
						new_st = String.valueOf(mistakes) + "\r\n" + String.valueOf(Double.valueOf(time)) + "\r\n"
								+String.valueOf(((int)speed))+"\r\n1";
					} else {
						int v = Integer.valueOf(lines.get(3));
						if (v==0) {
							new_st = String.valueOf(Double.valueOf(Double.valueOf(lines.get(0)) + mistakes)/2) + "\r\n" +
									String.valueOf(Double.valueOf(Double.valueOf(lines.get(1))+time)/2) + "\r\n" +
									String.valueOf(Double.valueOf(Double.valueOf(lines.get(2))+speed)/2) + "\r\n" + String.valueOf(Integer.valueOf(lines.get(3))+1);
						}
						else {
							new_st = String.valueOf(Double.valueOf(Double.valueOf(lines.get(0))*v + mistakes)/Double.valueOf(v+1)) + "\r\n" +
									String.valueOf(Double.valueOf(Double.valueOf(lines.get(1))*v+time)/Double.valueOf(v+1)) + "\r\n" +
									String.valueOf(Double.valueOf(Double.valueOf(lines.get(2))*v+speed)/Double.valueOf(v+1)) + "\r\n" + String.valueOf(Integer.valueOf(lines.get(3))+1);
						}

					}
					new_stat.write(new_st);
					new_stat.flush();



				}
				//System.out.println("+");
			}
			else
			{
				playBadMusic();
				//System.out.println("-");
				watcher.addMistake();
			}
		}
	}

	private void playGoodMusic()
	{
		music.play();
		if (msToReducing > 0)
			msToReducing = VOLUME_REDUCING_DELAY;
		else
		{
			new Thread(() ->
			{
				msToReducing = VOLUME_REDUCING_DELAY;
				//Задержка до начала снижения громкости
				while (msToReducing > 0)
				{
					msToReducing -= VOLUME_REDUCING_STEP;
					try
					{
						Thread.sleep(VOLUME_REDUCING_STEP);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				//Постепенное снижение громкости
				while (msToReducing == 0 && music != null && music.getVolume() > 0)
				{
					music.setVolume(music.getVolume() - 0.1);
					try
					{
						Thread.sleep(VOLUME_REDUCING_STEP);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				if (music != null)
				{
					if (msToReducing == 0)
						music.pause();
					music.setVolume(1.0);
				}
			}).start();
		}
	}

	private void playBadMusic()
	{
		if (falseNote != null)
		{
			MediaPlayer buf = falseNote;
			new Thread(() ->
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				buf.dispose();
			}).start();
		}
		falseNote = new MediaPlayer(new Media(new File("src/typingtrainer/PracticeScene/music/false_note_" + (int)(1 + Math.random() * 1) + ".mp3").toURI().toString()));
		falseNote.play();
		music.pause();
	}

	public static void setOptions(Word.Languages lang, int difficulty, boolean register){
		PracticeSceneController.lang = lang;
		PracticeSceneController.difficulty = difficulty;
		PracticeSceneController.register = register;
	}

	private void disposeSounds()
	{
		msToReducing = 0;
		if (music != null)
		{
			music.stop();
			music.dispose();
			music = null;
		}
		if (falseNote != null)
		{
			falseNote.stop();
			falseNote.dispose();
			falseNote = null;
		}
	}

	private void updHighlights()
	{
		char currChar = watcher.getCurrentChar();
		boolean isShift;
		if (currChar == ' ')
		{
			highlightRct.setVisible(false);
			highlightLShiftRct.setVisible(false);
			highlightRShiftRct.setVisible(false);
			highlightSpaceRct.setVisible(true);
		}
		else
		{
			String[] alphabet;
			switch (watcher.getLang())
			{
				case RU:
				default:
					alphabet = Word.ALPH_RU;
					break;
				case EN:
					alphabet = Word.ALPH_EN;
			}
			int i;
			if (alphabet[0].indexOf(currChar) != -1)
			{
				i = alphabet[0].indexOf(currChar);
				isShift = false;
			}
			else
			{
				i = alphabet[1].indexOf(currChar);
				isShift = true;
			}

			highlightRct.setVisible(true);
			highlightRct.setLayoutX(keyCoordinates[i][0]);
			highlightRct.setLayoutY(keyCoordinates[i][1]);
			highlightLShiftRct.setVisible(isShift);
			highlightRShiftRct.setVisible(isShift);
			highlightSpaceRct.setVisible(false);
		}
	}

	public void onRestartLabelClicked(MouseEvent mouseEvent)
	{
		restart();
	}
}