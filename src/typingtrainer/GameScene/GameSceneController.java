package typingtrainer.GameScene;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import typingtrainer.Game.Game;
import typingtrainer.Game.PvpObject;
import typingtrainer.Game.Ship;
import typingtrainer.ManagedScene;
import typingtrainer.PregameServerScene.PregameServerSceneController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

/**
 * Created by Meow on 22.04.2017.
 */
public class GameSceneController
{
	private static final int DEFAULT_SCREEN_WIDTH = 1280;
	private static final int DEFAULT_SCREEN_HEIGHT = 720;
	private static final int dt = 15;
	private static final int BACKGROUND_SPEED = 2;

	public static final String SHOT_CODEGRAM = "SHOT";
	public static final String DISCONNECT_CODEGRAM = "BYE";

	private Socket socket;
	private DataOutputStream ostream;
	private ManagedScene scene;
	private Image bg1img, bg2img;
	private double bg1Y, bg2Y;

	private boolean isRendering;
	private boolean isPlaying;

	private EventHandler<KeyEvent> onKeyPressed = new EventHandler<KeyEvent>()
	{
		@Override
		public void handle(KeyEvent event)
		{
			if (event.getCode() == KeyCode.ESCAPE)
			{
				try
				{
					ostream.writeUTF(DISCONNECT_CODEGRAM + ":");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				disconnect();
			}
		}
	};

	private Game game;
	public GameSceneController(ManagedScene scene, Socket socket)
	{
		System.out.println("Игровая сцена готова!" + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
		this.socket = socket;
		try
		{
			ostream = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		this.scene = scene;
		Group root = (Group) scene.getRoot();
		Canvas canvas = new Canvas(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
		//canvas.addEventHandler(KeyEvent.KEY_PRESSED, onKeyPressed);
		//canvas.setOnKeyPressed(onKeyPressed);
		canvas.setOnKeyPressed(onKeyPressed);
		canvas.setFocusTraversable(true);
		root.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		bg1img = bg2img = new Image("typingtrainer/GameScene/sea_background.png");
		bg1Y = 0.0;
		bg2Y = 0.0;
		game = new Game();

		isRendering = true;
		new Thread(() ->
		{
			try
			{
				while (isRendering)
				{
					Platform.runLater(() -> render(gc));
					Thread.sleep(dt);
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}).start();

		isPlaying = true;
		new Thread(this::waitForMessages).start();
	}

	private void handleIncomingMessage(String msg)
	{
		System.out.println(msg);
		if (msg.indexOf(':') >= 0)
		{
			final String codegram = msg.substring(0, msg.indexOf(':'));
			final String content = msg.substring(msg.indexOf(':') + 1);
			if (codegram.equals(PregameServerSceneController.DISCONNECT_CODEGRAM))
			{
				disconnect();
				System.out.println("Соединение разорвано (из игры)");
			}
		}
	}

	private void waitForMessages()
	{
		try
		{
			DataInputStream istream = new DataInputStream(socket.getInputStream());
			while (isPlaying)
			{
				String receivedData = istream.readUTF();
				handleIncomingMessage(receivedData);
			}
		}
		catch (EOFException e)
		{
			System.out.println("Bitch");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void updateModel()
	{
		;
	}

	private void render(GraphicsContext gc)
	{
		double sceneWidth = scene.getWidth();
		double sceneHeight = scene.getHeight();

		//Scaling
		gc.getCanvas().setWidth(sceneWidth);
		gc.getCanvas().setHeight(sceneHeight);
		double xScale = sceneWidth / DEFAULT_SCREEN_WIDTH;
		double yScale = sceneHeight / DEFAULT_SCREEN_HEIGHT;
		double bgSize = DEFAULT_SCREEN_WIDTH * Math.max(xScale, yScale);

		//Background
		if (bg1Y >= sceneHeight)
		{
			double buf = bg1Y;
			bg1Y = bg2Y;
			bg2Y = buf;
		}
		bg1Y += BACKGROUND_SPEED;
		bg2Y = bg1Y - bg2img.getHeight() + BACKGROUND_SPEED;
		gc.drawImage(bg1img, 0, bg1Y, bgSize, bgSize);
		gc.drawImage(bg1img, 0, bg2Y, bgSize, bgSize);

		//Ships
		for (int i = 0; i < Game.SHIPS_COUNT; ++i)
		{
			Ship ship = game.getShip(i);
			renderPvpObject(gc, ship, sceneWidth, 1, yScale);

			//Cannons
			renderPvpObject(gc, ship.getDefenciveCannon(), sceneWidth, 1, yScale);
			for (int j = 0; j < Ship.OFFENCIVE_CANNONS_COUNT; ++j)
				renderPvpObject(gc, ship.getOffenciveCannon(j), sceneWidth, 1, yScale);
		}
	}

	private void renderPvpObject(GraphicsContext gc, PvpObject object, double sceneWidth, double horizontalScale, double verticalScale)
	{
		double objectX, objectWidth;
		if (object.getBelonging() == PvpObject.Belonging.FRIENDLY)
		{
			objectX = object.getPosition().getX();
			objectWidth = object.getImage().getWidth();
		}
		else
		{
			objectX = sceneWidth - object.getPosition().getX();
			objectWidth = -object.getImage().getWidth();
		}
		gc.drawImage(object.getImage(), objectX, object.getPosition().getY(), objectWidth * horizontalScale, object.getImage().getHeight() * verticalScale);
	}

	private void disconnect()
	{
		isRendering = false;
		isPlaying = false;
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Platform.runLater(() ->
		{
			try
			{
				scene.getManager().popScene();
				scene.getManager().popScene();
			}
			catch (InvocationTargetException e)
			{
				System.out.println(e.getMessage());
			}
		});
	}
}
