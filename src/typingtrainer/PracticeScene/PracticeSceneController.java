package typingtrainer.PracticeScene;

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

/**
 * Created by Meow on 25.02.2017.
 */
public class PracticeSceneController
{
	public GridPane pane;
	public Label mainMenuLabel;
	public Label displayableStringLabel;
	private PracticeWatcher watcher;
	private Media media;
	private MediaPlayer mediaPlayer;

	static Word.Languages lang;
	static int difficulty;
	static boolean register;

	public void initialize()
	{
		System.out.println("Сцена практики готова!");

		System.out.println("Lang: " + PracticeSceneController.lang);
		System.out.println("Difficulty: " + PracticeSceneController.difficulty);
		System.out.println("Register: " + PracticeSceneController.register);

		displayableStringLabel.setFocusTraversable(true);
		watcher = new PracticeWatcher(new StringBuffer(Word.generateRndWord(20, PracticeSceneController.difficulty, PracticeSceneController.lang, PracticeSceneController.register)),
				PracticeSceneController.lang, PracticeSceneController.difficulty, PracticeSceneController.register);
		displayableStringLabel.setText(watcher.getDisplayableString());
		media = new Media(new File("src/typingtrainer/PracticeScene/music/practice_" + (int)(1 + Math.random() * 6) + ".mp3").toURI().toString());
		mediaPlayer = new MediaPlayer(media);

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
		try
		{
			((ManagedScene)(((Label)mouseEvent.getSource()).getScene())).getManager().popAllExceptFirst();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
		mediaPlayer.stop();
	}

	public void onKeyPressed(KeyEvent keyEvent)
	{
		if (!keyEvent.getCode().toString().equals("CONTROL") &&
				!keyEvent.getCode().toString().equals("SHIFT") &&
				!keyEvent.getCode().toString().equals("ALT") &&
				!keyEvent.getCode().toString().equals("ALT_GRAPH"))
		{
			boolean isSymbolCorrect;
			System.out.println(keyEvent.getText().charAt(0));
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
				mediaPlayer.play();
				watcher.passCurrentChar();
				if (watcher.getDisplayableString().length() != 0)
					displayableStringLabel.setText(watcher.getDisplayableString());
				else
				{
					mediaPlayer.stop();
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
				System.out.println("+");
			}
			else
			{
				mediaPlayer.pause();
				System.out.println("-");
				watcher.addMistake();
			}
		}
	}

	public static void setOptions(Word.Languages lang, int difficulty, boolean register){
		PracticeSceneController.lang = lang;
		PracticeSceneController.difficulty = difficulty;
		PracticeSceneController.register = register;
	}
}
