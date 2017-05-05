package typingtrainer.GameScene;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import typingtrainer.Game.*;
import typingtrainer.ManagedScene;
import typingtrainer.PregameServerScene.PregameServerSceneController;
import typingtrainer.Word;

import java.io.*;
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
	private static final int WORD_OFFSET_Y = 30;
	private static final Color BEFORE_FILL_COLOR = new Color(1, 1, 1, 0.2);
	private static final Color BEFORE_STROKE_COLOR = new Color(0, 0, 0, 0.2);
	private static final Color AFTER_FILL_COLOR = new Color(1, 0, 0, 1);
	private static final Color AFTER_STROKE_COLOR = new Color(0, 0, 0, 1);
	public static final int BACKGROUND_SPEED = 2;

	public static final String SEPARATOR_CODEGRAM = "&";
	public static final String OFFENCIVE_SHOT_CODEGRAM = "OFFSHOT";
	public static final String DEFENCIVE_SHOT_CODEGRAM = "DEFSHOT";
	public static final String DISCONNECT_CODEGRAM = "BYE";

	private MediaPlayer shotMP;

	private Socket socket;
	private DataOutputStream ostream;
	private ManagedScene scene;
	private Image bg1img, bg2img;
	private double bg1Y, bg2Y;

	private boolean isRendering;
	private boolean isPlaying;
	private Game game;

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
			else if (event.getCode() == KeyCode.SPACE) //Offencive
			{
				String shotInfo = game.shootOffenciveFriendly();
				if (!shotInfo.isEmpty())
				{
					try
					{
						ostream.writeUTF(shotInfo);
					}
					catch (IOException e)
					{
						System.out.println(e.getMessage());
					}
					playShotSound();
				}
			}
			else if (event.getCode() == KeyCode.ENTER) //Defencive
			{
				if (game.shootDefencive())
					playShotSound();
			}
			else if (!event.getText().isEmpty() && isShootableChar(event))
			{
				game.handleShootableChar(event);
			}
		}
	};

	private boolean isShootableChar(KeyEvent event)
	{
		//Проверять, не пустой ли текст
		return (event.getCode().isLetterKey() ||
				event.getText().charAt(0) == ',' ||
				event.getText().charAt(0) == '[' ||
				event.getText().charAt(0) == ';' ||
				event.getText().charAt(0) == '.' ||
				event.getText().charAt(0) == ']' ||
				event.getText().charAt(0) == '\'' ||
				event.getText().charAt(0) == '/' ||
				event.getText().charAt(0) == '\\' ||
				event.getText().charAt(0) == 'б' ||
				event.getText().charAt(0) == 'х' ||
				event.getText().charAt(0) == 'ж' ||
				event.getText().charAt(0) == 'ю' ||
				event.getText().charAt(0) == 'ъ' ||
				event.getText().charAt(0) == 'э' ||
				event.getText().charAt(0) == '.');
	}

	public GameSceneController(ManagedScene scene, Socket socket)
	{
		System.out.println("Игровая сцена готова!"/* + socket.getInetAddress().getHostAddress() + ":" + socket.getPort()*/);
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
		gc.setFont(new Font("Courier New Bold", 40));
		gc.setLineWidth(1.5);

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
					game.tick(dt);
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
			switch (codegram)
			{
				case PregameServerSceneController.DISCONNECT_CODEGRAM:
					disconnect();
					System.out.println("Соединение разорвано (из игры)");
					break;
				case OFFENCIVE_SHOT_CODEGRAM:
					String[] data = content.split(SEPARATOR_CODEGRAM);
					int cannonID = Integer.parseInt(data[0]);
					double targetX = Double.parseDouble(data[1]);
					double targetY = Double.parseDouble(data[2]);
					double speed = Double.parseDouble(data[3]);
					game.shootOffenciveHostile(cannonID, new Point2D(targetX, targetY), speed, data[4]);
					playShotSound();
					break;
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
			System.out.println("GameSceneController::waitForMessages - EOFException");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
		bg2Y = bg1Y - bg2img.getHeight() + BACKGROUND_SPEED * 10 * yScale;
		gc.drawImage(bg1img, 0, bg1Y, bgSize, bgSize);
		gc.drawImage(bg1img, 0, bg2Y, bgSize, bgSize);

		//Cannonballs
		for (int i = 0; i < game.getCannonballs().size(); ++i)
			renderPvpObject(gc, game.getCannonballs().get(i), sceneWidth, xScale, yScale);


		//Ships
		for (int i = 0; i < Game.SHIPS_COUNT; ++i)
		{
			Ship ship = game.getShip(i);
			renderPvpObject(gc, ship, sceneWidth, xScale, yScale);

			//Cannons
			renderPvpObject(gc, ship.getDefenciveCannon(), sceneWidth, xScale, yScale);
			for (int j = 0; j < Ship.OFFENCIVE_CANNONS_COUNT; ++j)
			{
				OffenciveCannon cannon = ship.getOffenciveCannon(j);
				renderPvpObject(gc, cannon, sceneWidth, xScale, yScale);
			}
		}

		//Smoke clouds
		for (int i = 0; i < game.getSmokeClouds().size(); ++i)
			renderPvpObject(gc, game.getSmokeClouds().get(i), sceneWidth, xScale, yScale);

		//Words
		//Offencive cannon words
		gc.setTextAlign(TextAlignment.LEFT);
		for (int i = 0; i < Ship.OFFENCIVE_CANNONS_COUNT; ++i)
		{
			OffenciveCannon cannon = game.getShip(0).getOffenciveCannon(i);
			String substrBefore = cannon.getWord().getSubstrBeforeWithSpaces(), substrAfter = cannon.getWord().getSubstrAfterWithSpaces();
			double x = 10 * xScale, y = (Ship.CANNON_BASE_POSITIONS[i + 1].getY() + cannon.getImage().getHeight() + WORD_OFFSET_Y) * yScale;
			gc.setFill(BEFORE_FILL_COLOR);
			gc.fillText(substrBefore, x, y);
			gc.setStroke(BEFORE_STROKE_COLOR);
			gc.strokeText(substrBefore, x, y);
			gc.setFill(AFTER_FILL_COLOR);
			gc.fillText(substrAfter, x, y);
			gc.setStroke(AFTER_STROKE_COLOR);
			gc.strokeText(substrAfter, x, y);
		}
		//Cannonball words
		gc.setTextAlign(TextAlignment.CENTER);
		for (int i = 0; i < game.getCannonballs().size(); ++i)
		{
			Cannonball cannonball = game.getCannonballs().get(i);
			if (cannonball.getBelonging() == PvpObject.Belonging.HOSTILE && cannonball.getType() == Cannonball.Type.OFFENCIVE)
			{
				String substrBefore = cannonball.getWord().getSubstrBeforeWithSpaces(), substrAfter = cannonball.getWord().getSubstrAfterWithSpaces();
				double x = (sceneWidth - (cannonball.getPosition().getX() - cannonball.getImage().getWidth() / 2)) * xScale,
						y = (cannonball.getPosition().getY() + cannonball.getImage().getHeight() + WORD_OFFSET_Y) * yScale;
				gc.setFill(BEFORE_FILL_COLOR);
				gc.fillText(substrBefore, x, y);
				gc.setStroke(BEFORE_STROKE_COLOR);
				gc.strokeText(substrBefore, x, y);
				gc.setFill(AFTER_FILL_COLOR);
				gc.fillText(substrAfter, x, y);
				gc.setStroke(AFTER_STROKE_COLOR);
				gc.strokeText(substrAfter, x, y);
			}
		}
	}

	//http://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas
	private void renderPvpObject(GraphicsContext gc, PvpObject object, double sceneWidth, double horizontalScale, double verticalScale)
	{
		double finalX, finalY, finalWidth, finalHeight, finalAngle, finalPivotX, finalPivotY;
		if (object.getBelonging() == PvpObject.Belonging.FRIENDLY)
		{
			finalX = object.getPosition().getX() * horizontalScale;
			finalWidth = object.getImage().getWidth() * horizontalScale;
			finalAngle = object.getRotationAngle();
			finalPivotX = finalX + object.getPivot().getX() * horizontalScale;
		}
		else
		{
			if (object.isHorFlipable())
			{
				finalX = sceneWidth - object.getPosition().getX() * horizontalScale;
				finalWidth = -object.getImage().getWidth() * horizontalScale;
				finalAngle = -object.getRotationAngle();
				finalPivotX = finalX - object.getPivot().getX() * horizontalScale;
			}
			else
			{
				finalX = sceneWidth - (object.getPosition().getX() + object.getImage().getWidth()) * horizontalScale;
				finalWidth = object.getImage().getWidth() * horizontalScale;
				finalAngle = object.getRotationAngle();
				finalPivotX = finalX + object.getPivot().getX() * horizontalScale;
			}
		}
		finalY = object.getPosition().getY() * verticalScale;
		finalHeight = object.getImage().getHeight() * verticalScale;
		finalPivotY = finalY + object.getPivot().getY() * verticalScale;

		gc.save(); // saves the current state on stack, including the current transform
		rotateGraphicsContext(gc, finalAngle, finalPivotX, finalPivotY);
		gc.drawImage(object.getImage(), finalX, finalY, finalWidth, finalHeight);
		gc.restore(); // back to original state (before rotation)
	}

	//http://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas
	private void rotateGraphicsContext(GraphicsContext gc, double angle, double x, double y)
	{
		Rotate r = new Rotate(angle, x, y);
		gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
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

	private void playShotSound()
	{
		if (shotMP != null)
		{
			MediaPlayer buf = shotMP;
			new Thread(() ->
			{
				try
				{
					Thread.sleep(2000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				buf.dispose();
			}).start();
		}
		shotMP = new MediaPlayer(new Media(new File("src/typingtrainer/GameScene/sounds/shot" + (int) (1 + Math.random() * 3) + ".mp3").toURI().toString()));
		shotMP.play();
	}

	public void setGameParams(Word.Languages lang, int difficulty, boolean isRegister)
	{
		game.setLangParam(lang);
		game.setDifficultyParam(difficulty);
		game.setRegisterParam(isRegister);
	}
}
