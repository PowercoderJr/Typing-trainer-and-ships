package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;

public class Controller
{

	public Label helloworld;

	public void sayHelloWorld(ActionEvent actionEvent)
	{
		helloworld.setText("WAZZZZZZUUUUUP");
	}
}