package typingtrainer.PregameServerScene;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import typingtrainer.ManagedScene;
import typingtrainer.ServerInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

/**
 * Created by Meow on 04.04.2017.
 */

public class PregameServerSceneController
{
	@FXML
	public GridPane pane;

	private static String serverName;
	private static String serverPassword;
	public static final String SEARCHING_CODEGRAM = "SEA";
	public static final String CONNECTING_CODEGRAM = "CON";

	private boolean isWaiting;
	private MulticastSocket mcSocket;
	private ServerSocket serverSocket;
	private Socket socket;
	private ServerInfo info;
	private String password;
	private String opponentIP;

	public void initialize()
	{
		System.out.println("Предигровая сцена готова!");
		password = serverPassword;
		try
		{
			info = new ServerInfo(serverName, InetAddress.getLocalHost().getHostAddress(), password.isEmpty() ? "" : "+");
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		isWaiting = true;
		opponentIP = "";

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
						String clientIP = receivedData.substring(SEARCHING_CODEGRAM.length() + 1);
						Socket s = new Socket(InetAddress.getByName(clientIP), 7913);
						ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
						out.writeObject(info);
						s.close();
					}
					/*else if (receivedData.substring(0, receivedData.indexOf(':')).equals(CONNECTING_CODEGRAM))
					{
						String clientIP = receivedData.substring(CONNECTING_CODEGRAM.length() + 1, receivedData.indexOf('|'));
						String clientPassword = receivedData.substring(receivedData.indexOf('|') + 1);

						Socket s = new Socket(InetAddress.getByName(clientIP), 7913);
						OutputStream out = s.getOutputStream();
						if (password.isEmpty() || clientPassword.equals(password))
						{
							out.write("Connection accepted".getBytes());
						}
						else
						{
							out.write("Connection declined".getBytes());
						}
						s.close();
					}*/
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
					in.read(bytes);
					String receivedData = new String(bytes).trim();
					//localSocket.close();
					//System.out.println("Получили!");
					System.out.println("TCP Received: \"" + receivedData + "\"");

					if (!receivedData.isEmpty() && receivedData.substring(0, receivedData.indexOf(':')).equals(CONNECTING_CODEGRAM))
					{
						String clientIP = receivedData.substring(CONNECTING_CODEGRAM.length() + 1, receivedData.indexOf('|'));
						String clientPassword = receivedData.substring(receivedData.indexOf('|') + 1);
						System.out.println(clientIP + " - " + clientPassword);

						if (opponentIP.isEmpty())
						{
							if (password.isEmpty() || clientPassword.equals(password))
							{
								opponentIP = clientIP;
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

	public static void setServerName(String serverName)
	{
		PregameServerSceneController.serverName = serverName;
	}

	public static void setServerPassword(String serverPassword)
	{
		PregameServerSceneController.serverPassword = serverPassword;
	}

	private void stopAnswerers() throws IOException
	{
		mcSocket.close();
		if (socket != null)
			socket.close();
		serverSocket.close();
		isWaiting = false;
	}
}
