package typingtrainer.PregameClientScene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import typingtrainer.ManagedScene;
import typingtrainer.PregameServerScene.PregameServerSceneController;
import typingtrainer.ServerInfo;
import typingtrainer.Word;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

/**
 * Created by Meow on 17.04.2017.
 */
public class PregameClientSceneController
{
	@FXML
	public GridPane pane;
	@FXML
	public ChoiceBox langCB;
	@FXML
	public ChoiceBox difficultyCB;
	@FXML
	public CheckBox registerChb;
	@FXML
	public TextField messageTF;
	@FXML
	public TextArea chatTA;

	private static String arg_username;
	private static String arg_serverIP;

	private Socket socket;
	private String username;
	private boolean isConnected;

	public void initialize()
	{
		System.out.println("Предигровая сцена готова!");
		username = arg_username;

		isConnected = true;
	}

	public void onBackClicked(MouseEvent mouseEvent)
	{
		//Закрыть сокеты

		try
		{
			((ManagedScene) (((Label) mouseEvent.getSource()).getScene())).getManager().popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public void onSendClicked(MouseEvent mouseEvent)
	{
		String message = username + ": " + messageTF.getText() + "\n";
		messageTF.clear();
		chatTA.appendText(message);
	}

	public void onMessageTFKeyPressed(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.ENTER)
			onSendClicked(null);
	}

	public static void setArg_username(String arg_username)
	{
		PregameClientSceneController.arg_username = arg_username;
	}

	public static void setArg_serverIP(String arg_serverIP)
	{
		PregameClientSceneController.arg_serverIP = arg_serverIP;
	}
}