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
import java.awt.im.InputContext;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
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
	public Rectangle highlightSpaceRct;
	private PracticeWatcher watcher;
	volatile private MediaPlayer music;
	volatile private MediaPlayer falseNote;
	private TimerTask reduceMusicVolumeTask;
	private Timer reduceMusicVolumeTimer;

	static Word.Languages lang;
	static int difficulty;
	static boolean register;
	private static boolean isReducingCanceled;
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
		displayableStringLabel.setFocusTraversable(true);
		watcher = new PracticeWatcher(new StringBuffer(Word.generateRndWord(20, PracticeSceneController.difficulty, PracticeSceneController.lang, PracticeSceneController.register)),
				PracticeSceneController.lang, PracticeSceneController.difficulty, PracticeSceneController.register);
		updHighlights();
		displayableStringLabel.setText(watcher.getDisplayableString());
		music = new MediaPlayer(new Media(new File("src/typingtrainer/PracticeScene/music/practice_" + (int)(1 + Math.random() * 6) + ".mp3").toURI().toString()));

		InputContext InCon = java.awt.im.InputContext.getInstance();
		InCon.selectInputMethod(new Locale("en", "US"));
		System.out.println(InCon.getLocale().toString());
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

	public void onKeyPressed(KeyEvent keyEvent)
	{
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
					alert.setContentText("Kras \noshibki: " + String.valueOf(watcher.getMistakeCount()) + "\nvremya: " + String.format("%.2f", (watcher.getFinalTime() * 1e-9)) + " секундочек");
					alert.showAndWait();
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
		try
		{
			reduceMusicVolumeTask.cancel();
			reduceMusicVolumeTimer.cancel();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		music.play();
		isReducingCanceled = true;
		reduceMusicVolumeTask = new TimerTask()
		{
			@Override
			public void run()
			{
				isReducingCanceled = false;
				while (!isReducingCanceled && music.getVolume() > 0)
				{
					music.setVolume(music.getVolume() - 0.1);
					try
					{
						TimeUnit.MILLISECONDS.sleep(25);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				if (!isReducingCanceled)
					music.pause();
				music.setVolume(1.0);
			}
		};
		reduceMusicVolumeTimer = new Timer();
		reduceMusicVolumeTimer.schedule(reduceMusicVolumeTask, 2000);
	}

	private void playBadMusic()
	{
		if (falseNote != null)
		{
			MediaPlayer buf = falseNote;
			TimerTask disposeTask = new TimerTask()
			{
				@Override
				public void run()
				{
					buf.dispose();
				}
			};
			Timer disposeTimer = new Timer();
			disposeTimer.schedule(disposeTask, 1000);
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
		try
		{
			reduceMusicVolumeTask.cancel();
			reduceMusicVolumeTimer.cancel();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		if (music != null)
		{
			music.stop();
			music.dispose();
		}
		if (falseNote != null)
		{
			falseNote.stop();
			falseNote.dispose();
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
}