package typingtrainer.PregameClientScene;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import typingtrainer.ManagedScene;
import typingtrainer.PregameServerScene.PregameServerSceneController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	private static Socket arg_socket;

	private Socket socket;
	private String username;
	private boolean isConnected;
	private OutputStream ostream;

	public void initialize()
	{
		System.out.println("Предигровая сцена готова!");
		socket = arg_socket;
		username = arg_username;
		isConnected = true;
		new Thread(() -> establishConnectionWithServer(socket)).start();
	}

	private void handleIncomingMessage(String msg)
	{
		System.out.println(msg);
		String codegram = msg.substring(0, msg.indexOf(':'));
		String content = msg.substring(msg.indexOf(':') + 1) + "\n";
		if (codegram.equals(PregameServerSceneController.CHAT_MSG_CODEGRAM))
			chatTA.appendText(content);
		else if (codegram.equals(PregameServerSceneController.DISCONNECT_CODEGRAM))
			isConnected = false;
	}

	private void establishConnectionWithServer(Socket socket)
	{
		try (Socket autoClosableSocket = socket)
		{
			InputStream in = socket.getInputStream();
			ostream = socket.getOutputStream();
			byte[] bytes = new byte[256];
			while (isConnected)
			{
				in.read(bytes);
				handleIncomingMessage(new String(bytes).trim());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void onBackClicked(MouseEvent mouseEvent)
	{
		//Закрыть сокеты
		if (isConnected)
		{
			try
			{
				ostream.flush();
				ostream.write((PregameServerSceneController.DISCONNECT_CODEGRAM + ":").getBytes());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

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
		sendChatMessage();
	}

	public void onMessageTFKeyPressed(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.ENTER)
			sendChatMessage();
	}

	private void sendChatMessage()
	{
		String msg = username + ": " + messageTF.getText() + "\n";
		messageTF.clear();
		chatTA.appendText(msg);

		if (isConnected)
		{
			try
			{
				ostream.flush();
				ostream.write((PregameServerSceneController.CHAT_MSG_CODEGRAM + ":" + msg).getBytes());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void setArg_username(String arg_username)
	{
		PregameClientSceneController.arg_username = arg_username;
	}

	public static void setArg_socket(Socket arg_socket)
	{
		PregameClientSceneController.arg_socket = arg_socket;
	}
}