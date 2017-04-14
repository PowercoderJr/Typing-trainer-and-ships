package typingtrainer.LobbyScene;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import typingtrainer.Main;
import typingtrainer.ManagedScene;
import typingtrainer.SceneManager;
import typingtrainer.ServerInfo;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

/**
 * Created by Meow on 04.04.2017.
 */

public class LobbySceneController
{
	private static final double NANOSECONDS_IN_MILLISECOND = 1e+6;
	private static final int SEARCHING_TIMEOUT = 5000;
	private long startSearchingTime;
	private boolean isSearching;

	@FXML
	public TableView serversTable;
	@FXML
	public Label refreshLabel;
	TableColumn nameColumn;
	TableColumn ipColumn;
	TableColumn passwordFlagColumn;

	private ObservableList<ServerInfo> servers = FXCollections.observableArrayList();

	public void initialize()
	{
		System.out.println("Сцена лобби готова!");
		nameColumn = buildTableColumn("Имя сервера", "name", 200);
		ipColumn = buildTableColumn("IP сервера", "ip", 200);
		passwordFlagColumn = buildTableColumn("Пароль", "passwordFlag", 100);
		Label ph = new Label("СЕРВЕРЫ НЕ НАЙДЕНЫ");
		ph.setStyle("-fx-font-size: 60px; \n-fx-effect: dropshadow( one-pass-box, white, 0, 0, 0, 0);");
		serversTable.setPlaceholder(ph);

		//servers.add(new ServerInfo("Some server", "192.193.194.195", "+"));
		serversTable.setItems(servers);
		serversTable.getColumns().addAll(nameColumn, ipColumn, passwordFlagColumn);
		refreshServerList();
	}

	private TableColumn buildTableColumn(String text, String property, int prefWidth)
	{
		TableColumn t = new TableColumn(text);
		t.setCellValueFactory(new PropertyValueFactory<ServerInfo, String>(property));
		t.setPrefWidth(prefWidth);
		return t;
	}

	public void onBackClicked(MouseEvent mouseEvent)
	{
		isSearching = false;
		try
		{
			((ManagedScene) (((Label) mouseEvent.getSource()).getScene())).getManager().popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	private void refreshServerList()
	{
		Platform.runLater(() -> refreshLabel.setDisable(true));
		servers.clear();
		new Thread(() ->
		{
			System.out.println("Обновление списка серверов");
			try (ServerSocket ss = new ServerSocket(7913);)
			{
				InetAddress address = InetAddress.getByName("230.1.2.3");
				MulticastSocket mcSocket = new MulticastSocket();
				String msg = InetAddress.getLocalHost().getHostAddress();
				DatagramPacket dgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, address, 7913);
				mcSocket.send(dgPacket);
				mcSocket.close();

				ss.setSoTimeout(SEARCHING_TIMEOUT);
				startSearchingTime = System.nanoTime();
				Socket s = null;
				boolean success = true;
				isSearching = true;
				while (System.nanoTime() - startSearchingTime < SEARCHING_TIMEOUT * NANOSECONDS_IN_MILLISECOND && isSearching)
				{
					try
					{
						s = ss.accept();
					}
					catch (SocketTimeoutException e)
					{
						System.out.println("SocketTimeout Exception");
						//e.printStackTrace();
						success = false;
						isSearching = false;
					}
					if (success)
					{
						ObjectInputStream in = new ObjectInputStream(s.getInputStream());
						final ServerInfo info = (ServerInfo) in.readObject();
						Platform.runLater(() -> servers.add(info));
						startSearchingTime = System.nanoTime();
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
				System.out.println("IO Exception");
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			System.out.println("Обновление завершено");
			Platform.runLater(() -> refreshLabel.setDisable(false));
		}).start();
	}

	public void onCreateClicked(MouseEvent mouseEvent) throws IOException
	{
		isSearching = false;
		SceneManager sceneManager = ((ManagedScene) (((Label) mouseEvent.getSource()).getScene())).getManager();
		Parent pregameSceneFXML = FXMLLoader.load(Main.class.getResource("PregameScene/pregameScene.fxml"));
		ManagedScene pregameScene = new ManagedScene(pregameSceneFXML, 1280, 720, sceneManager);
		pregameScene.getStylesheets().add("typingtrainer/pregameScene/style.css");
		sceneManager.pushScene(pregameScene);
	}

	public void onJoinClicked(MouseEvent mouseEvent)
	{
		//СЕРВЕР
		new Thread(() ->
		{
			System.out.println("Ожидание подключения");
			try
			{
				DatagramSocket s = new DatagramSocket(7913);
				while (true)
				{
					DatagramPacket p = new DatagramPacket(new byte[256], 256);
					s.receive(p);
					byte[] buf = p.getData();
					System.out.println(new String(buf, 0, buf.length));
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Конец ожидания");
		}).start();

		//КЛИЕНТ
		/*
        new Thread(() -> {
            System.out.println("Попытка подключения");
            try {
                InetAddress address = InetAddress.getLocalHost();
                DatagramSocket s = new DatagramSocket(7913, address);
                byte[] buf = ("WAZZUP").getBytes();
                DatagramPacket p = new DatagramPacket(buf, 0, buf.length, InetAddress.getByName("192.168.0.***"), 7913);
                s.send(p);
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Конец подключения");
        }).start();
        */
	}

	public void onRefreshClicked(MouseEvent mouseEvent)
	{
		refreshServerList();
	}
}
