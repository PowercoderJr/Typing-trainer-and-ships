package typingtrainer.PregameScene;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import typingtrainer.ManagedScene;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.*;

/**
 * Created by Meow on 04.04.2017.
 */

public class PregameSceneController
{
	private boolean isWaiting;
	private MulticastSocket mcSocket;

	public void initialize()
	{
		System.out.println("Предигровая сцена готова!");
		isWaiting = true;
		new Thread(() ->
		{
			System.out.println("Ожидание клиента");
			try
			{
				mcSocket = new MulticastSocket(7913);
				InetAddress group = InetAddress.getByName("230.1.2.3");
				mcSocket.joinGroup(group);

				while (isWaiting)
				{
					DatagramPacket dgPacket = new DatagramPacket(new byte[256], 256);
					System.out.println("Сейчас получим!");
					mcSocket.receive(dgPacket);
					System.out.println("Получили!");
					String receivedData = new String(dgPacket.getData()).trim();
					System.out.println("Received: \"" + receivedData + "\"");
					if (receivedData.equals("Is anybody waiting for me?"))
						System.out.println("Me!!!");
				}
				mcSocket.close();
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
			System.out.println("Ожидание клиента завершено");
		}).start();
	}

	public void onBackClicked(MouseEvent mouseEvent)
	{
		mcSocket.close();
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
