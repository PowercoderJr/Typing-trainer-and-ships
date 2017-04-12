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
import typingtrainer.PracticeScene.PracticeSceneController;
import typingtrainer.SceneManager;
import typingtrainer.Word;

import javax.xml.crypto.Data;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

/**
 * Created by Meow on 04.04.2017.
 */

public class LobbySceneController
{

	public class ServerInfo
	{
		private SimpleStringProperty name;
		private SimpleStringProperty ip;
		private SimpleStringProperty passwordFlag;

		public ServerInfo(String name, String ip, String passwordFlag)
		{
			this.name = new SimpleStringProperty(name);
			this.ip = new SimpleStringProperty(ip);
			this.passwordFlag = new SimpleStringProperty(passwordFlag);
		}

		public String getName()
		{
			return name.get();
		}

		public SimpleStringProperty nameProperty()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name.set(name);
		}

		public String getIp()
		{
			return ip.get();
		}

		public SimpleStringProperty ipProperty()
		{
			return ip;
		}

		public void setIp(String ip)
		{
			this.ip.set(ip);
		}

		public String getPasswordFlag()
		{
			return passwordFlag.get();
		}

		public SimpleStringProperty passwordFlagProperty()
		{
			return passwordFlag;
		}

		public void setPasswordFlag(String passwordFlag)
		{
			this.passwordFlag.set(passwordFlag);
		}
	}

	@FXML
	public TableView serversTable;
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
		new Thread(() ->
		{
			System.out.println("Обновление списка серверов");
			try
			{
				InetAddress address = InetAddress.getByName("230.1.2.3");
				MulticastSocket mcSocket = new MulticastSocket();
				String msg = "Is anybody waiting for me?";
				DatagramPacket dgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, address, 7913);
				mcSocket.send(dgPacket);
				mcSocket.close();
			}
			catch (SocketException e)
			{
				System.out.println("Socket Exception");
				e.printStackTrace();
			}
			catch (IOException e)
			{
				System.out.println("IO Exception");
				e.printStackTrace();
			}
			System.out.println("Обновление завершено");
		}).start();
	}

	public void onCreateClicked(MouseEvent mouseEvent) throws IOException
	{
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
