package typingtrainer.PregameServerScene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import typingtrainer.ManagedScene;
import typingtrainer.ServerInfo;
import typingtrainer.Word;

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

	private static String arg_serverName;
	private static String arg_serverPassword;
	public static final String CONNECTION_ACCEPTED_MSG = "ConnectionAccepted";
	public static final String CONNECTION_DECLINED_MSG = "ConnectionDeclined";
	public static final String CHAT_MSG_CODEGRAM = "CHAT_MSG";
	public static final String DISCONNECT_CODEGRAM = "BYE";

	private boolean isWaiting;
	private MulticastSocket mcSocket;
	private ServerSocket serverSocket;
	private Socket socket;
	private ServerInfo info;
	private String username;
	private String password;
	private String opponentIP;
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

		//Отвечающий на поиск серверов
		new Thread(this::answerTheSearchRequests).start();

		//Отвечающий на запросы подключения оппонентов
		new Thread(this::answerTheConnectionRequests).start();
	}

	private void handleIncomingMessage(String msg)
	{
		msg = msg.substring(0, msg.length() - 1);
		System.out.println(msg);
		String codegram = msg.substring(0, msg.indexOf(':'));
		String content = msg.substring(msg.indexOf(':') + 1);
		if (codegram.equals(PregameServerSceneController.CHAT_MSG_CODEGRAM))
			chatTA.appendText(content + '\n');
		else if (codegram.equals(DISCONNECT_CODEGRAM))
			opponentIP = "";
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
					System.out.println("Socket Exception");
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
			System.out.println("Socket Exception");
			//e.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("IO Exception:");
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
					//localSocket.close();
					//System.out.println("Получили!");
					System.out.println("TCP Received: \"" + receivedData + "\"");

					if (!receivedData.isEmpty())
					{
						String opponentIP = receivedData.substring(0, receivedData.indexOf('|'));
						String opponentPassword = receivedData.substring(receivedData.indexOf('|') + 1);
						System.out.println(opponentIP + " - " + opponentPassword);

						out.flush();
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
					}
				}
				catch (SocketTimeoutException e)
				{
					System.out.println("SocketTimeout Exception");
					//e.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void establishConnectionWithOpponent(Socket socket)
	{
		try (Socket autoClosableSocket = socket)
		{
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
			e.printStackTrace();
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
		}

		try
		{
			stopAnswerers();
		}
		catch (IOException e)
		{
			e.printStackTrace();
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

	public void onStartClicked(MouseEvent mouseEvent)
	{
		try
		{
			stopAnswerers();
		}
		catch (IOException e)
		{
			e.printStackTrace();
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

	private void stopAnswerers() throws IOException
	{
		mcSocket.close();
		if (socket != null)
			socket.close();
		serverSocket.close();
		isWaiting = false;
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
