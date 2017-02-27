package typingtrainer.PracticeScene;

import javafx.event.EventHandler;
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
import java.util.Locale;

/**
 * Created by Meow on 25.02.2017.
 */
public class PracticeSceneController
{
	public GridPane pane;
	public Label backLabel;
	private Word currStr;
	private PracticeWatcher watcher;




	public void initialize()
	{
		System.out.println("Сцена практики готова!");
		backLabel.setFocusTraversable(true);
		currStr = new Word();
		StringBuffer stringParam = new StringBuffer(currStr.GetWord());
		watcher = new PracticeWatcher(stringParam);
		backLabel.setText(watcher.GetDisplayableString().toString());



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

	public void checkLetter(KeyEvent keyEvent) {

			if (keyEvent.getText().length()!=0)
			{
			if (keyEvent.getText().charAt(0) == watcher.GetCurrentChar())
			{
				watcher.PassCurrentChar();
				if (watcher.GetDisplayableString().length()!=0)
				{
					backLabel.setText(watcher.GetDisplayableString());
				}
				else
					{
						backLabel.setText("");
						Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setTitle("Поздравляем");
						alert.setHeaderText(null);

						alert.setContentText("Kras \n oshibki: "+ String.valueOf(watcher.getMistakeCount()) + "\n vremya: " + String.format("%.2f", (watcher.GetFinalTime()*1e-9)) + " секундочек");
						alert.showAndWait();
					}
			}
			else{
				System.out.println("incorrect");
				watcher.AddMistake();
			}

			}



	}
}
