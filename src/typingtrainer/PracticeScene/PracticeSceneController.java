package typingtrainer.PracticeScene;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import typingtrainer.ManagedScene;
import typingtrainer.PracticeWatcher;
import typingtrainer.Word;

import java.awt.im.InputContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Meow on 25.02.2017.
 */
public class PracticeSceneController
{
	public GridPane pane;
	public Label backLabel;
	private PracticeWatcher watcher;

	public void initialize()
	{
		System.out.println("Сцена практики готова!");
		backLabel.setFocusTraversable(true);
		watcher = new PracticeWatcher(new StringBuffer(Word.generateRndWord(20, 33, Word.Languages.EN, true)), Word.Languages.EN);
		backLabel.setText(watcher.getDisplayableString());

		InputContext InCon = java.awt.im.InputContext.getInstance();
		Locale en = new Locale("en", "US");
		InCon.selectInputMethod(en);
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


	public void onBackLabelClicked(MouseEvent mouseEvent)
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

	public void onKeyPressed(KeyEvent keyEvent)
	{
		if (!keyEvent.getCode().toString().equals("CONTROL") &&
				!keyEvent.getCode().toString().equals("SHIFT") &&
				!keyEvent.getCode().toString().equals("ALT") &&
				!keyEvent.getCode().toString().equals("ALT_GRAPH"))
		{
			boolean isSymbolCorrect;
			if (keyEvent.getText().length() > 0)
			{
				if (watcher.getCurrentChar() == ' ')
					isSymbolCorrect = keyEvent.getText().charAt(0) == ' ';
				else
				{
					if (keyEvent.isShiftDown())
					{
						char symbolWithShift;
						char[][] alphabet;
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
						int symbolIndex = alphabet[0].toString().indexOf(keyEvent.getText().charAt(0)); //Не катит, и Arrays.toString() тоже
						if (symbolIndex >= 0 && symbolIndex < Word.MAX_LEVEL)
						{
							symbolWithShift = alphabet[1][symbolIndex];
							isSymbolCorrect = symbolWithShift == watcher.getCurrentChar();
						}
						else
							isSymbolCorrect = false;
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
				watcher.passCurrentChar();
				if (watcher.getDisplayableString().length() != 0)
					backLabel.setText(watcher.getDisplayableString());
				else
				{
					backLabel.setText("");
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Поздравляем");
					alert.setHeaderText(null);

					alert.setContentText("Kras \n oshibki: " + String.valueOf(watcher.getMistakeCount()) + "\n vremya: " + String.format("%.2f", (watcher.getFinalTime() * 1e-9)) + " секундочек");
					alert.showAndWait();
				}
				System.out.println("+");
			}
			else
			{
				System.out.println("-");
				watcher.addMistake();
			}
		}
	}
}
