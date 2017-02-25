package typingtrainer.MainScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import typingtrainer.Main;

public class MainSceneController
{
	public void initialize()
	{
		System.out.println("Главная сцена готова!");
	}

	public void labelClicked(MouseEvent mouseEvent)
	{
		Label lbl = (Label)mouseEvent.getSource();
		lbl.setText("WAZZZZZUUUUUP!!!");
	}

	public void practiceModeLabelClicked(MouseEvent mouseEvent)
	{
		//((Stage)((Label)mouseEvent.getSource()).getScene().getWindow()).setScene(practiceScene);
	}
}