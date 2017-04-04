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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Никитка on 28.02.2017.
 */

public class PregameSceneController
{
	private boolean isWaiting;
	private DatagramSocket dgSocket;

	public void initialize()
	{
		System.out.println("Предигровая сцена готова!");
		isWaiting = true;
		new Thread(() ->
		{
			try
			{
				dgSocket = new DatagramSocket();
				while (isWaiting)
				{
					DatagramPacket dgPacket = new DatagramPacket(new byte[1024], 1024);
					dgSocket.receive(dgPacket);
					String recievedData = new String(dgPacket.getData());
					System.out.println("Recieved: " + recievedData);
					if (recievedData.equals("Is anybody waiting for me?"))
					{
						System.out.println("Me!!!");
						isWaiting = false;
					}
				}
			}
			catch (SocketException e)
			{
				System.out.println("Socket Exception:");
				e.printStackTrace();
			}
			catch (IOException e)
			{
				System.out.println("IO Exception:");
				e.printStackTrace();
			}
		}).start();
	}

	public void onBackClicked(MouseEvent mouseEvent)
	{
		dgSocket.close();
		isWaiting = false;
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
		;
	}
}
