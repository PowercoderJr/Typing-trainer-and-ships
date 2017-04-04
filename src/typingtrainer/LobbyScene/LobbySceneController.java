package typingtrainer.LobbyScene;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Никитка on 28.02.2017.
 */

public class LobbySceneController
{
	private class ServerInfo
	{
		private SimpleStringProperty name;
		private SimpleStringProperty ip;
		private SimpleStringProperty passwordFlag;

		public ServerInfo(String name, String ip, String passwordFlag)
		{
			this.name.set(name);
			this.ip.set(ip);
			this.passwordFlag.set(passwordFlag);
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
		ipColumn = buildTableColumn("IP сервера", "ip", 150);
		passwordFlagColumn = buildTableColumn("Пароль", "password", 100);
		Label ph = new Label("СЕРВЕРЫ НЕ НАЙДЕНЫ");
		ph.setStyle("-fx-font-size: 60px; \n-fx-effect: dropshadow( one-pass-box, white, 0, 0, 0, 0);");
		serversTable.setPlaceholder(ph);
		
		servers.add(new ServerInfo("Pisosina", "192.0.0.1", "*****"));
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

	public void onCreateClicked(MouseEvent mouseEvent)
	{
		;
	}

	public void onJoinClicked(MouseEvent mouseEvent)
	{
		;
	}
}
