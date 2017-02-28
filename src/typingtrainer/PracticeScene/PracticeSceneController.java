package typingtrainer.PracticeScene;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
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


	static int lang;
	static int difficulti;
	static int register;


	public void initialize()
	{
		System.out.println("Сцена практики готова!");

		System.out.println(PracticeSceneController.lang);
		System.out.println(PracticeSceneController.difficulti);
		System.out.println(PracticeSceneController.register);

		int diff = 0;
		if (PracticeSceneController.difficulti==16){
			 diff = 33;
		}
		else {
			 diff = (PracticeSceneController.difficulti+1)*2;
		}


		Word.Languages lang = PracticeSceneController.lang == 0 ? Word.Languages.RU  : Word.Languages.EN;
		boolean reg = PracticeSceneController.register == 0 ? true : false;



		backLabel.setFocusTraversable(true);
		watcher = new PracticeWatcher(new StringBuffer(Word.generateRndWord(20, diff, lang, reg)), lang);
		backLabel.setText(watcher.getDisplayableString());

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
				watcher.passCurrentChar();
				if (watcher.getDisplayableString().length() != 0)
					backLabel.setText(watcher.getDisplayableString());
				else
				{
					backLabel.setText("");
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
				System.out.println("-");
				watcher.addMistake();
			}
		}
	}


	public static void setOptions(int lang, int difficulti, int register){
		PracticeSceneController.lang = lang;
		PracticeSceneController.difficulti = difficulti;
		PracticeSceneController.register = register;

	}
}
