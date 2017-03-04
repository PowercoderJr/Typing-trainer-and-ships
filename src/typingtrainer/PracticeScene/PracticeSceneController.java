package typingtrainer.PracticeScene;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import typingtrainer.ManagedScene;
import typingtrainer.PracticeWatcher;
import typingtrainer.Word;

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
	private PracticeWatcher watcher;
	volatile private MediaPlayer music;
	volatile private MediaPlayer falseNote;
	private TimerTask reduceMusicVolumeTask;
	private Timer reduceMusicVolumeTimer;

	static Word.Languages lang;
	static int difficulty;
	static boolean register;
	static boolean isReducingCanceled;

	public void initialize()
	{
		System.out.println("Сцена практики готова!");

		displayableStringLabel.setFocusTraversable(true);
		watcher = new PracticeWatcher(new StringBuffer(Word.generateRndWord(20, PracticeSceneController.difficulty, PracticeSceneController.lang, PracticeSceneController.register)),
				PracticeSceneController.lang, PracticeSceneController.difficulty, PracticeSceneController.register);
		displayableStringLabel.setText(watcher.getDisplayableString());
		music = new MediaPlayer(new Media(new File("src/typingtrainer/PracticeScene/music/practice_" + (int)(1 + Math.random() * 6) + ".mp3").toURI().toString()));

		InputContext InCon = java.awt.im.InputContext.getInstance();
		InCon.selectInputMethod(new Locale("en", "US"));
		System.out.println(InCon.getLocale().toString());

		/*
		pane.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.out.println(event.getCharacter());
				System.out.println(event.getCode());
				System.out.println(event.getText());
				System.out.println("sdfsdf");
			}
		});
		*/
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

				watcher.passCurrentChar();
				if (watcher.getDisplayableString().length() != 0)
					displayableStringLabel.setText(watcher.getDisplayableString());
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
				//System.out.println("-");
				watcher.addMistake();
			}
		}
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
}
