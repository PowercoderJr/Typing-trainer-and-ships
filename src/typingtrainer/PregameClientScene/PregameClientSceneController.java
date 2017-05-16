package typingtrainer.PregameClientScene;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import typingtrainer.GameScene.GameSceneController;
import typingtrainer.InfoScene.InfoSceneController;
import typingtrainer.Main;
import typingtrainer.ManagedScene;
import typingtrainer.PregameServerScene.PregameServerSceneController;
import typingtrainer.SceneManager;
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
	public TextField messageTF;
	@FXML
	public TextArea chatTA;
	@FXML
	public Label backLabel;
	@FXML
	public Label serverNameLabel;
	@FXML
	public Label langLabel;
	@FXML
	public Label difficultyLabel;
	@FXML
	public Label registerLabel;

	private static String arg_username;
	private static Socket arg_socket;

	private Socket socket;
	private String username;
	private boolean isConnected;
	private DataOutputStream ostream;

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
		if (msg.indexOf(':') >= 0)
		{
			final String codegram = msg.substring(0, msg.indexOf(':'));
			final String content = msg.substring(msg.indexOf(':') + 1);
			switch (codegram)
			{
				case PregameServerSceneController.CHAT_MSG_CODEGRAM:
					chatTA.appendText(content + '\n');
					break;
				case PregameServerSceneController.START_CODEGRAM:
					/**Уязвимый участок в случае запуска 2х клиентов на 1 компе*/
					Platform.runLater(() ->
					{
						isConnected = false;

						Word.Languages lang;
						switch (langLabel.getText())
						{
							case "Русский":
							default:
								lang = Word.Languages.RU;
								break;
							case "English":
								lang = Word.Languages.EN;
								break;
						}

						String diffStr = difficultyLabel.getText();
						int difficulty = Integer.parseInt(diffStr.substring(0, diffStr.indexOf(' ')));

						SceneManager sceneManager = ((ManagedScene) (pane.getScene())).getManager();
						Group root = new Group();
						ManagedScene gameScene = new ManagedScene(root, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT, Color.LIGHTBLUE, sceneManager);
						GameSceneController controller = new GameSceneController(gameScene, socket, lang, difficulty, registerLabel.getText().substring(registerLabel.getText().indexOf(':') + 2).equals("ДА"));
						controller.setPlayerNames(username, serverNameLabel.getText());
						gameScene.getStylesheets().add("typingtrainer/GameScene/style.css");
						sceneManager.pushScene(gameScene);
					});
					try
					{
						ostream.writeUTF("OK:");
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					/**--------------------------------------*/
					break;
				case PregameServerSceneController.DISCONNECT_CODEGRAM:
					disconnect();
					Platform.runLater(() ->
					{
						onBackClicked(null);
						System.out.println("Соединение разорвано");
						try
						{
							InfoSceneController.setInfo("Сервер разорвал соединение");
							SceneManager sceneManager = ((ManagedScene) (pane.getScene())).getManager();
							Parent infoSceneFXML = FXMLLoader.load(Main.class.getResource("InfoScene/infoScene.fxml"));
							ManagedScene infoScene = new ManagedScene(infoSceneFXML, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT, sceneManager);
							infoScene.getStylesheets().add("typingtrainer/infoScene/style.css");
							sceneManager.pushScene(infoScene);
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
					});
					break;
				case PregameServerSceneController.SETTINGS_SERV_NAME_CODEGRAM:
					Platform.runLater(() -> serverNameLabel.setText(content));
					break;
				case PregameServerSceneController.SETTINGS_LANG_CODEGRAM:
					Platform.runLater(() -> langLabel.setText(content));
					break;
				case PregameServerSceneController.SETTINGS_DIFFICULTY_CODEGRAM:
					Platform.runLater(() -> difficultyLabel.setText(content));
					break;
				case PregameServerSceneController.SETTINGS_REGISTER_CODEGRAM:
					Platform.runLater(() -> registerLabel.setText(content));
					break;
			}
		}
	}

	private void establishConnectionWithServer(Socket socket)
	{
		try
		{
			System.out.println("Соединение установлено");
			DataInputStream in = new DataInputStream(socket.getInputStream());
			ostream = new DataOutputStream(socket.getOutputStream());
			ostream.writeUTF(PregameServerSceneController.SET_NAME_CODEGRAM + ":" + username);
			ostream.writeUTF(PregameServerSceneController.CHAT_MSG_CODEGRAM + ":* " + username + " подключился");
			ostream.writeUTF(PregameServerSceneController.GET_SETTINGS_CODEGRAM + ":");
			while (isConnected)
			{
				String receivedData = in.readUTF();
				handleIncomingMessage(receivedData);
			}
		}
		catch (IOException e)
		{
			//e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public void onBackClicked(MouseEvent mouseEvent)
	{
		//Закрыть сокеты
		if (isConnected)
		{
			try
			{
				ostream.writeUTF(PregameServerSceneController.CHAT_MSG_CODEGRAM + ":* " + username + " отключился");
				ostream.writeUTF(PregameServerSceneController.DISCONNECT_CODEGRAM + ":");
				ostream.flush();
			}
			catch (IOException e)
			{
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
			disconnect();
		}

		try
		{
			((ManagedScene) (pane.getScene())).getManager().popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void disconnect()
	{
		isConnected = false;
		if (socket != null && !socket.isClosed())
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
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
		String msg = messageTF.getText();
		if (!msg.trim().isEmpty())
		{
			msg = username + ": " + msg;
			messageTF.clear();
			chatTA.appendText(msg + "\n");

			if (isConnected)
			{
				try
				{
					ostream.writeUTF(PregameServerSceneController.CHAT_MSG_CODEGRAM + ":" + msg);
					ostream.flush();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
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