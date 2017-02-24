package typingtrainer;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Controller
{

	public Label helloworld;

	public void sayHelloWorld(ActionEvent actionEvent)
	{
		helloworld.setText("WAZZZZZZUUUUUP!!!");
	}

	public void labelClicked(MouseEvent mouseEvent)
	{
		Label lbl = (Label)mouseEvent.getSource();
		lbl.setText("LALALEY!!!");
	}
}