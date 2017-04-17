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
	public static final String SEARCHING_CODEGRAM = "SEA";
	public static final String CONNECTING_CODEGRAM = "CON";

	private boolean isWaiting;
	private MulticastSocket mcSocket;
	private ServerSocket serverSocket;
	private Socket socket;
	private ServerInfo info;
	private String username;
	private String password;
	private String opponentIP;

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
					if (receivedData.substring(0, receivedData.indexOf(':')).equals(SEARCHING_CODEGRAM))
					{
						String opponentIP = receivedData.substring(SEARCHING_CODEGRAM.length() + 1);
						Socket s = new Socket(InetAddress.getByName(opponentIP), 7913);
						ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
						out.writeObject(info);
						s.close();
					}
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
				try (Socket localSocket = localServerSocket.accept())
				{
					socket = localSocket;
					InputStream in = localSocket.getInputStream();
					OutputStream out = localSocket.getOutputStream();

					byte[] bytes = new byte[256];
					int length = in.read(bytes);
					String receivedData = new String(bytes, 0, length);
					//localSocket.close();
					//System.out.println("Получили!");
					System.out.println("TCP Received: \"" + receivedData + "\"");

					if (!receivedData.isEmpty() && receivedData.substring(0, receivedData.indexOf(':')).equals(CONNECTING_CODEGRAM))
					{
						String opponentIP = receivedData.substring(CONNECTING_CODEGRAM.length() + 1, receivedData.indexOf('|'));
						String opponentPassword = receivedData.substring(receivedData.indexOf('|') + 1);
						System.out.println(opponentIP + " - " + opponentPassword);

						if (this.opponentIP.isEmpty())
						{
							if (password.isEmpty() || opponentPassword.equals(password))
							{
								this.opponentIP = opponentIP;
								out.flush();
								out.write(CONNECTION_ACCEPTED_MSG.getBytes());
							}
							else
								out.write(CONNECTION_DECLINED_MSG.getBytes());
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

	private void establishConnectionWithOpponent()
	{


		try (ServerSocket personalServerSocket = new ServerSocket(7915))
		{
			while (!opponentIP.isEmpty())
			{
				//System.out.println("Сейчас получим!");
				try (Socket socket = personalServerSocket.accept())
				{
					InputStream in = socket.getInputStream();
					OutputStream out = socket.getOutputStream();

					byte[] bytes = new byte[256];
					int length = in.read(bytes);
					String receivedData = new String(bytes, 0, length);
					//localSocket.close();
					//System.out.println("Получили!");
					System.out.println("TCP Received: \"" + receivedData + "\"");

					if (!receivedData.isEmpty() && receivedData.substring(0, receivedData.indexOf(':')).equals(CONNECTING_CODEGRAM))
					{
						String opponentIP = receivedData.substring(CONNECTING_CODEGRAM.length() + 1, receivedData.indexOf('|'));
						String opponentPassword = receivedData.substring(receivedData.indexOf('|') + 1);
						System.out.println(opponentIP + " - " + opponentPassword);

						if (this.opponentIP.isEmpty())
						{
							if (password.isEmpty() || opponentPassword.equals(password))
							{
								this.opponentIP = opponentIP;
								out.flush();
								out.write("Connection accepted".getBytes());
							}
							else
								out.write("Connection declined".getBytes());
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

	public void onBackClicked(MouseEvent mouseEvent)
	{
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
		String message = username + ": " + messageTF.getText() + "\n";
		messageTF.clear();
		chatTA.appendText(message);
	}

	public void onMessageTFKeyPressed(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.ENTER)
			onSendClicked(null);
	}
}
