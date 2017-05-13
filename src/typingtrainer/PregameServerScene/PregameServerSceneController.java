package typingtrainer.PregameServerScene;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import typingtrainer.*;
import typingtrainer.GameScene.GameSceneController;
import typingtrainer.MainScene.MainSceneController;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

/**
 * Created by Meow on 04.04.2017.
 */

// 7913 - для поиска
// 7914 - для попытки соединения
// 7915 - для оппонента
public class PregameServerSceneController
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
	@FXML
	public Label startLabel;
	@FXML
	public Label opponentNameLabel;
	@FXML
	public ImageView kickImg;

	private static String arg_serverName;
	private static String arg_serverPassword;
	private static final Rectangle2D KICK_IMG1 = new Rectangle2D(0, 100, 100, 100);
	private static final Rectangle2D KICK_IMG2 = new Rectangle2D(100, 100, 100, 100);
	public static final String CONNECTION_ACCEPTED_MSG = "ConnectionAccepted";
	public static final String CONNECTION_DECLINED_MSG = "ConnectionDeclined";
	public static final String NO_OPPONENT_YET_STR = "Здесь пусто";
	public static final String CHAT_MSG_CODEGRAM = "CHAT_MSG";
	public static final String START_CODEGRAM = "START";
	public static final String DISCONNECT_CODEGRAM = "BYE";
	public static final String SET_NAME_CODEGRAM = "SET_NAME";
	public static final String GET_SETTINGS_CODEGRAM = "GET_STG";
	public static final String SETTINGS_SERV_NAME_CODEGRAM = "STG_SENA";
	public static final String SETTINGS_LANG_CODEGRAM = "STG_LANG";
	public static final String SETTINGS_DIFFICULTY_CODEGRAM = "STG_DIFF";
	public static final String SETTINGS_REGISTER_CODEGRAM = "STG_REG";

	private boolean isWaiting;
	private MulticastSocket mcSocket;
	private ServerSocket serverSocket;
	private Socket socket;
	private ServerInfo info;
	private String username;
	private String password;
	private String opponentIP;
	private String opponentName;
	private DataOutputStream ostream;

	public void initialize()
	{
		System.out.println("Предигровая сцена готова!");
		//Формирование инфы о сервере
		username = arg_serverName;
		password = arg_serverPassword;
		try
		{
			info = new ServerInfo(username, InetAddress.getLocalHost().getHostAddress(), password.isEmpty() ? "" : "+");
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		isWaiting = true;
		opponentIP = "";
		kickImg.setImage(MainSceneController.BUTTONS_SPRITESHEET);
		kickImg.setViewport(KICK_IMG1);

		//Автоаполнение настроек
		ObservableList<String> levels = FXCollections.observableArrayList();
		for (int i = 0; i < 30; i += 2)
			levels.add(new String() + (i + 2) + " (" + Word.ALPH_RU[0].charAt(i) + Word.ALPH_RU[0].charAt(i + 1) +
					" / " + Word.ALPH_EN[0].charAt(i) + Word.ALPH_EN[0].charAt(i + 1) + ")");
		levels.add("33" + " (" + Word.ALPH_RU[0].charAt(30) + Word.ALPH_RU[0].charAt(31) + Word.ALPH_RU[0].charAt(32) +
				" / " + Word.ALPH_EN[0].charAt(30) + Word.ALPH_EN[0].charAt(31) + Word.ALPH_EN[0].charAt(32) + ")");
		langCB.setItems(FXCollections.observableArrayList("Русский", "English"));
		difficultyCB.setItems(levels);

		int language, difficulty;
		boolean register;
		try (
				FileReader settings_read = new FileReader("src/typingtrainer/ModScene/Settings/settings.txt");
				BufferedReader reader = new BufferedReader(settings_read);)
		{
			language = Integer.valueOf(reader.readLine());
			difficulty = Integer.valueOf(reader.readLine());
			register = Integer.valueOf(reader.readLine()) == 1;
		}
		catch (Exception e)
		{
			language = 0;
			difficulty = 0;
			register = false;
		}
		langCB.getSelectionModel().select(language);
		difficultyCB.getSelectionModel().select(difficulty);
		registerChb.setSelected(register);

		/*
			http://stackoverflow.com/questions/14522680/javafx-choicebox-events
			http://stackoverflow.com/questions/11918095/which-choicebox-event-to-choose
		*/
		langCB.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) ->
		{
			if (!opponentIP.isEmpty())
				sendLangTextAt((int)number2);
		});
		difficultyCB.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) ->
		{
			if (!opponentIP.isEmpty())
				sendDifficultyTextAt((int)number2);
		});
		//http://stackoverflow.com/questions/13726824/javafx-event-triggered-when-selecting-a-check-box
		registerChb.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if (!opponentIP.isEmpty())
				sendRegisterOption();
		});

		//Отвечающий на поиск серверов
		new Thread(this::answerTheSearchRequests).start();

		//Отвечающий на запросы подключения оппонентов
		new Thread(this::answerTheConnectionRequests).start();
	}

	private void handleIncomingMessage(String msg)
	{
		System.out.println(msg);
		if (msg.indexOf(':') >= 0)
		{
			String codegram = msg.substring(0, msg.indexOf(':'));
			String content = msg.substring(msg.indexOf(':') + 1);
			//System.out.println("CODEGRAM - " + codegram + "\nCONTENT - " + content);
			switch (codegram)
			{
				case PregameServerSceneController.CHAT_MSG_CODEGRAM:
					chatTA.appendText(content + '\n');
					break;
				case DISCONNECT_CODEGRAM:
					opponentIP = "";
					opponentName = "";
					try
					{
						socket.close();
					}
					catch (IOException e)
					{
						System.out.println(e.getMessage() + ": IO Exception - PregameServerSceneController::handleIncomingMessage");
						//e.printStackTrace();
					}
					System.out.println("Соединение разорвано");
					setOpponentControlsActive(false);
					Platform.runLater(() -> opponentNameLabel.setText(NO_OPPONENT_YET_STR));
					break;
				case SET_NAME_CODEGRAM:
					opponentName = content;
					Platform.runLater(() -> opponentNameLabel.setText(opponentName));
					break;
				case GET_SETTINGS_CODEGRAM:
					sendServerName();
					sendLangTextAt(langCB.getSelectionModel().getSelectedIndex());
					sendDifficultyTextAt(difficultyCB.getSelectionModel().getSelectedIndex());
					sendRegisterOption();
					break;
			}
		}
	}

	private void answerTheSearchRequests()
	{
		System.out.println("Ожидание поискового запроса");
		try
		{
			mcSocket = new MulticastSocket(7913);
			InetAddress group = InetAddress.getByName("230.1.2.3");
			mcSocket.joinGroup(group);

			while (isWaiting)
			{
				DatagramPacket dgPacket = new DatagramPacket(new byte[256], 256);
				//System.out.println("Сейчас получим!");
				try
				{
					mcSocket.receive(dgPacket);
				}
				catch (SocketException e)
				{
					System.out.println(e.getMessage() + ": Socket Exception - PregameServerSceneController::answerTheSearchRequests");
					//e.printStackTrace();
					isWaiting = false;
				}
				//System.out.println("Получили!");
				String receivedData = new String(dgPacket.getData()).trim();
				System.out.println("UDP Received: \"" + receivedData + "\"");

				if (!receivedData.isEmpty())
				{
					Socket s = new Socket(InetAddress.getByName(receivedData), 7913);
					ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
					out.writeObject(info);
					s.close();
				}
			}
		}
		catch (SocketException e)
		{
			System.out.println(e.getMessage() + ": Socket Exception - PregameServerSceneController::answerTheSearchRequests");
			//e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage() + ": IO Exception - PregameServerSceneController::answerTheSearchRequests");
			e.printStackTrace();
		}
		finally
		{
			mcSocket.close();
		}
		System.out.println("Ожидание поискового запроса завершено");
	}

	private void answerTheConnectionRequests()
	{
		try (ServerSocket localServerSocket = new ServerSocket(7914))
		{
			serverSocket = localServerSocket;
			while (isWaiting)
			{
				//System.out.println("Сейчас получим!");
				try
				{
					Socket localSocket = localServerSocket.accept();
					socket = localSocket;

					DataInputStream in = new DataInputStream(localSocket.getInputStream());
					DataOutputStream out = new DataOutputStream(localSocket.getOutputStream());

					String receivedData = in.readUTF();
					//System.out.println("Получили!");
					System.out.println("TCP Received: \"" + receivedData + "\"");

					if (!receivedData.isEmpty())
					{
						String opponentIP = receivedData.substring(0, receivedData.indexOf('|'));
						String opponentPassword = receivedData.substring(receivedData.indexOf('|') + 1);
						System.out.println(opponentIP + " - " + opponentPassword);

						if (this.opponentIP.isEmpty() && (password.isEmpty() || opponentPassword.equals(password)))
						{
							this.opponentIP = opponentIP;
							new Thread(() -> establishConnectionWithOpponent(localSocket)).start();
							out.writeUTF(CONNECTION_ACCEPTED_MSG);
						}
						else
						{
							out.writeUTF(CONNECTION_DECLINED_MSG);
							localSocket.close();
						}
						out.flush();
					}
				}
				catch (SocketTimeoutException e)
				{
					System.out.println(e.getMessage() + ": SocketTimeout Exception - PregameServerSceneController::answerTheConnectionRequests");
					//e.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage() + ": IO Exception - PregameServerSceneController::answerTheConnectionRequests");
			//e.printStackTrace();
			//e.printStackTrace();
		}
	}

	private void establishConnectionWithOpponent(Socket socket)
	{
		try
		{
			System.out.println("Соединение установлено");
			setOpponentControlsActive(true);
			DataInputStream istream = new DataInputStream(socket.getInputStream());
			ostream = new DataOutputStream(socket.getOutputStream());
			while (!opponentIP.isEmpty())
			{
				String receivedData = istream.readUTF();
				handleIncomingMessage(receivedData);
			}
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage() + ": IO Exception - PregameServerSceneController::establishConnectionWithOpponent");
			//e.printStackTrace();
		}
	}

	public void onBackClicked(MouseEvent mouseEvent)
	{
		if (!opponentIP.isEmpty())
		{
			try
			{
				ostream.writeUTF(DISCONNECT_CODEGRAM + ":");
				ostream.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			opponentIP = "";
		}

		try
		{
			disconnect();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
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

	public void onStartClicked(MouseEvent mouseEvent) throws IOException
	{
		try
		{
			//disconnect();
			opponentIP = "";
			mcSocket.close();
			serverSocket.close();
			ostream.writeUTF(START_CODEGRAM + ":");
			ostream.writeUTF("OK:");

			Word.Languages lang;
			switch (langCB.getSelectionModel().getSelectedIndex())
			{
				case 0:
				default:
					lang = Word.Languages.RU;
					break;
				case 1:
					lang = Word.Languages.EN;
					break;
			}

			String diffStr = difficultyCB.getSelectionModel().getSelectedItem().toString();
			int difficulty = Integer.parseInt(diffStr.substring(0, diffStr.indexOf(' ')));

			SceneManager sceneManager = ((ManagedScene) (((Label) mouseEvent.getSource()).getScene())).getManager();
			Group root = new Group();
			ManagedScene gameScene = new ManagedScene(root, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT, Color.LIGHTBLUE, sceneManager);
			GameSceneController controller = new GameSceneController(gameScene, socket, lang, difficulty, registerChb.isSelected());
			controller.setPlayerNames(username, opponentName);
			gameScene.getStylesheets().add("typingtrainer/GameScene/style.css");
			sceneManager.pushScene(gameScene);
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void setArg_serverName(String arg_serverName)
	{
		PregameServerSceneController.arg_serverName = arg_serverName;
	}

	public static void setArg_serverPassword(String arg_serverPassword)
	{
		PregameServerSceneController.arg_serverPassword = arg_serverPassword;
	}

	private void disconnect() throws IOException
	{
		isWaiting = false; //Было внизу
		mcSocket.close();
		if (socket != null)
			socket.close();
		serverSocket.close();
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

			if (!opponentIP.isEmpty())
			{
				try
				{
					ostream.writeUTF(CHAT_MSG_CODEGRAM + ":" + msg);
					ostream.flush();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void sendServerName()
	{
		try
		{
			ostream.writeUTF(SETTINGS_SERV_NAME_CODEGRAM + ":" + username);
		}
			catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void sendLangTextAt(int index)
	{
		try
		{
			ostream.writeUTF(SETTINGS_LANG_CODEGRAM + ":" + langCB.getItems().get(index).toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void sendDifficultyTextAt(int index)
	{
		try
		{
			ostream.writeUTF(SETTINGS_DIFFICULTY_CODEGRAM + ":" + difficultyCB.getItems().get(index).toString());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void sendRegisterOption()
	{
		try
		{
			ostream.writeUTF(SETTINGS_REGISTER_CODEGRAM + ":" + (registerChb.isSelected() ? "ДА" : "НЕТ"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void onKickClicked(MouseEvent mouseEvent)
	{
		if (!opponentIP.isEmpty())
		{
			try
			{
				ostream.writeUTF(DISCONNECT_CODEGRAM + ":");
				ostream.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			chatTA.appendText("* " + opponentName + " изгнан\n");
			opponentIP = "";
			Platform.runLater(() -> opponentNameLabel.setText(NO_OPPONENT_YET_STR));
			setOpponentControlsActive(false);
		}
	}

	private void setOpponentControlsActive(boolean isOpponentJoined)
	{
		kickImg.setVisible(isOpponentJoined);
		startLabel.setDisable(!isOpponentJoined);
	}

	public void onKickMouseEntered(MouseEvent mouseEvent)
	{
		kickImg.setViewport(KICK_IMG2);
	}

	public void onKickMouseExited(MouseEvent mouseEvent)
	{
		kickImg.setViewport(KICK_IMG1);
	}
}
