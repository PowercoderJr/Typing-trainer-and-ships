package typingtrainer.GameScene;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
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
import java.util.ArrayList;

/**
 * Created by Meow on 22.04.2017.
 */
public class GameSceneController
{
	//Debug
	public static class Line
	{
		double x1, y1, x2, y2;

		public Line(double x1, double y1, double x2, double y2)
		{
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	}
	public static final ArrayList<Line> lines = new ArrayList<>();
	public static final ArrayList<Point2D> points = new ArrayList<>();
	public static final ArrayList<Point2D> colPoints = new ArrayList<>();
	//

	public static final int DEFAULT_SCREEN_WIDTH = 1280;
	public static final int DEFAULT_SCREEN_HEIGHT = 720;
	public static final int BACKGROUND_SPEED = 2;
	private static final int dt = 15;

	private static final int WORD_OFFSET_Y = 30;
	private static final int PLAYER_NAME_POS_X = 135;
	private static final int PLAYER_NAME_POS_Y = 40;
	private static final int PLAYER_NAME_MAX_WIDTH = 260;
	private static final int PLAYER_HP_BAR_POS_X = 5;
	private static final int PLAYER_HP_BAR_POS_Y = 55;
	private static final int PLAYER_HP_MAX_WIDTH = 120;


	private static final Color BEFORE_FILL_COLOR = new Color(1, 1, 1, 0.2);
	private static final Color BEFORE_STROKE_COLOR = new Color(0, 0, 0, 0.2);
	private static final Color AFTER_FILL_COLOR = new Color(1, 0, 0, 1);
	private static final Color AFTER_STROKE_COLOR = new Color(0, 0, 0, 1);
	private static final Color PLAYER_NAME_FILL_COLOR = new Color(1.0/256*123, 1.0/256*197, 1.0/256*35, 1);

	public static final String SEPARATOR_CODEGRAM = "&";
	public static final String OFFENCIVE_SHOT_CODEGRAM = "OFFSHOT";
	public static final String DEFENCIVE_SHOT_CODEGRAM = "DEFSHOT";
	public static final String DISCONNECT_CODEGRAM = "BYE";

	private MediaPlayer shotMP;
	private MediaPlayer ballsCollisionMP;
	private MediaPlayer shipHitMP;

	private Socket socket;
	private DataOutputStream ostream;
	private ManagedScene scene;
	private Image bgImg;
	private double bg1Y, bg2Y;
	private Image hpBarBackground;

	private double pregameTimer;
	private boolean isGameProceed;
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
			else if (isGameProceed)
			{
				if (event.getCode() == KeyCode.SPACE) //Offencive
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
					game.setAllCharsDoneToZero();
				}
				else if (event.getCode() == KeyCode.ENTER) //Defencive
				{
					String shotInfo = game.shootDefenciveFriendly();
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
					game.setAllCharsDoneToZero();
				}
				else if (!event.getText().isEmpty() && isShootableChar(event))
				{
					game.handleShootableChar(event);
				}
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

	public static Point2D mirrorRelativelyToDefaultWidth(Point2D point)
	{
		return new Point2D(DEFAULT_SCREEN_WIDTH - point.getX(), point.getY());
	}

	public static double mirrorRelativelyToDefaultWidth(double x)
	{
		return DEFAULT_SCREEN_WIDTH - x;
	}



	public GameSceneController(ManagedScene scene, Socket socket, Word.Languages lang, int difficulty, boolean isRegister)
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
		canvas.setOnKeyPressed(onKeyPressed);
		canvas.setFocusTraversable(true);
		root.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(1.5);

		bgImg = new Image("typingtrainer/GameScene/sea_background.png");
		bg1Y = 0.0;
		bg2Y = 0.0;
		game = new Game(lang, difficulty, isRegister);
		hpBarBackground = new WritableImage(Game.SPRITE_SHEET.getPixelReader(), 132, 349, 255, 26);

		isGameProceed = false;
		new Thread(() ->
		{
			//Задержка перед стартом
			try
			{
				pregameTimer = 3.0;
				do
				{
					Platform.runLater(() -> renderPrepairingStage(gc, "" + (int) Math.ceil(pregameTimer)));
					Thread.sleep(dt);
					pregameTimer -= dt / 1000.0;
				} while (pregameTimer > 0);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			//Интерфейс
			new Thread(() ->
			{
				try
				{
					isGameProceed = true;
					while (isGameProceed)
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

			//Логика
			new Thread(() ->
			{
				try
				{
					while (isGameProceed)
					{
						if (game.isNewBallsCollisionDetected())
						{
							game.setNewBallsCollisionDetected(false);
							playBallsCollisionSound();
						}
						if (game.isNewShipDamageDetected())
						{
							game.setNewShipDamageDetected(false);
							playShipDamagedSound();
						}
						Thread.sleep(dt);
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}).start();

			//Ожидание сообщений
			new Thread(this::waitForMessages).start();

		}).start();

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
				{
					String[] data = content.split(SEPARATOR_CODEGRAM);
					int cannonID = Integer.parseInt(data[0]);
					double targetX = Double.parseDouble(data[1]);
					double targetY = Double.parseDouble(data[2]);
					double speed = Double.parseDouble(data[3]);
					game.shootOffenciveHostile(cannonID, new Point2D(targetX, targetY), speed, data[4]);
					playShotSound();
					break;
				}
				case DEFENCIVE_SHOT_CODEGRAM:
				{
					String[] data = content.split(SEPARATOR_CODEGRAM);
					double targetX = Double.parseDouble(data[0]);
					double targetY = Double.parseDouble(data[1]);
					double speed = Double.parseDouble(data[2]);
					game.shootDefenciveHostile(new Point2D(targetX, targetY), speed, data[3]);
					playShotSound();
				}
			}
		}
	}

	private void waitForMessages()
	{
		try
		{
			DataInputStream istream = new DataInputStream(socket.getInputStream());
			while (isGameProceed)
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

	private void renderBackground(GraphicsContext gc, double sceneHeight, double bgSize)
	{
		if (bg1Y >= sceneHeight)
		{
			double buf = bg1Y;
			bg1Y = bg2Y;
			bg2Y = buf;
		}
		bg1Y += BACKGROUND_SPEED;
		bg2Y = bg1Y - bgSize + BACKGROUND_SPEED * 2;
		gc.drawImage(bgImg, 0, bg1Y, bgSize, bgSize);
		gc.drawImage(bgImg, 0, bg2Y, bgSize, bgSize);
	}

	private void renderShips(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
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
	}

	private void renderHpBars(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		gc.setFill(PLAYER_NAME_FILL_COLOR);
		gc.setTextAlign(TextAlignment.CENTER);
		for (int i = 0; i < Game.SHIPS_COUNT; ++i)
		{
			Ship ship = game.getShip(i);
			if (ship.getHp() > 0)
			{
				Image hpBar = new WritableImage(Game.SPRITE_SHEET.getPixelReader(), 132, 375, (int) (hpBarBackground.getWidth() * ship.getHp() / Ship.BASE_HP), 26);
				renderPlayerImage(gc, hpBarBackground, ship.getBelonging(), PLAYER_HP_BAR_POS_X, PLAYER_HP_BAR_POS_Y, sceneWidth, xScale, yScale);
				renderPlayerImage(gc, hpBar, ship.getBelonging(), PLAYER_HP_BAR_POS_X, PLAYER_HP_BAR_POS_Y, sceneWidth, xScale, yScale);

				gc.setFont(new Font("Arial Bold", 42));
				renderPlayerText(gc, ship.getPlayerName(), true, true, ship.getBelonging(), PLAYER_NAME_POS_X, PLAYER_NAME_POS_Y, PLAYER_NAME_MAX_WIDTH, sceneWidth, xScale, yScale);
				gc.setFont(new Font("Arial Bold", 20));
				renderPlayerText(gc, (int) ship.getHp() + " / " + Ship.BASE_HP, false, true, ship.getBelonging(), PLAYER_HP_BAR_POS_X + 65, PLAYER_HP_BAR_POS_Y + 21, PLAYER_HP_MAX_WIDTH, sceneWidth, xScale, yScale);
			}
		}
	}

	private void renderTimer(GraphicsContext gc, String text, double sceneWidth, double sceneHeight)
	{
		gc.setFont(new Font("Arial Bold", 200));
		gc.setFill(Color.WHITE);
		gc.fillText(text, sceneWidth / 2, sceneHeight / 2);
		gc.strokeText(text, sceneWidth / 2, sceneHeight / 2);
	}

	private void renderCannonballs(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		for (int i = 0; i < game.getCannonballs().size(); ++i)
		{
			renderPvpObject(gc, game.getCannonballs().get(i), sceneWidth, xScale, yScale);
			//Debug
			points.add(new Point2D(game.getCannonballs().get(i).getBelonging() == PvpObject.Belonging.HOSTILE ? DEFAULT_SCREEN_WIDTH - game.getCannonballs().get(i).getPosition().getX() : game.getCannonballs().get(i).getPosition().getX(), game.getCannonballs().get(i).getPosition().getY()));
			//
		}
	}

	private void renderSmokeClouds(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		for (int i = 0; i < game.getSmokeClouds().size(); ++i)
			renderPvpObject(gc, game.getSmokeClouds().get(i), sceneWidth, xScale, yScale);
	}

	private void renderCannonballShards(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		for (int i = 0; i < game.getCannonballShards().size(); ++i)
			renderPvpObject(gc, game.getCannonballShards().get(i), sceneWidth, xScale, yScale);
	}

	private void renderWoodenSplinters(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		for (int i = 0; i < game.getSplinterPiles().size(); ++i)
			for (int j = 0; j < WoodenSplintersPile.SPLINTERS_COUNT; ++j)
				renderPvpObject(gc, game.getSplinterPiles().get(i).getSplinter(j), sceneWidth, xScale, yScale);
	}

	private void renderOffenciveCannonWords(GraphicsContext gc, double xScale, double yScale)
	{
		gc.setTextAlign(TextAlignment.LEFT);
		for (int i = 0; i < Ship.OFFENCIVE_CANNONS_COUNT; ++i)
		{
			OffenciveCannon cannon = game.getShip(0).getOffenciveCannon(i);
			String substrBefore = cannon.getPvpWord().getSubstrBeforeWithSpaces(), substrAfter = cannon.getPvpWord().getSubstrAfterWithSpaces();
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
	}

	private void renderCannonballWords(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		gc.setTextAlign(TextAlignment.CENTER);
		for (int i = 0; i < game.getCannonballs().size(); ++i)
		{
			Cannonball cannonball = game.getCannonballs().get(i);
			if (cannonball.getBelonging() == PvpObject.Belonging.HOSTILE && cannonball.getType() == Cannonball.Type.OFFENCIVE && cannonball.canBeCountershooted())
			{
				String substrBefore = cannonball.getPvpWord().getSubstrBeforeWithSpaces(), substrAfter = cannonball.getPvpWord().getSubstrAfterWithSpaces();
				double x = sceneWidth - (cannonball.getPosition().getX() + cannonball.getPivot().getX()) * xScale,
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

	private void renderPrepairingStage(GraphicsContext gc, String text)
	{
		double sceneWidth = scene.getWidth();
		double sceneHeight = scene.getHeight();

		//Scaling
		gc.getCanvas().setWidth(sceneWidth);
		gc.getCanvas().setHeight(sceneHeight);
		double xScale = sceneWidth / DEFAULT_SCREEN_WIDTH;
		double yScale = sceneHeight / DEFAULT_SCREEN_HEIGHT;
		double bgSize = DEFAULT_SCREEN_WIDTH * Math.max(xScale, yScale);

		renderBackground(gc, sceneHeight, bgSize);
		renderShips(gc, sceneWidth, xScale, yScale);
		renderHpBars(gc, sceneWidth, xScale, yScale);
		renderTimer(gc, text, sceneWidth, sceneHeight);
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

		renderBackground(gc, sceneHeight, bgSize);
		renderWoodenSplinters(gc, sceneWidth, xScale, yScale);
		renderCannonballs(gc, sceneWidth, xScale, yScale);
		renderShips(gc, sceneWidth, xScale, yScale);
		renderSmokeClouds(gc, sceneWidth, xScale, yScale);
		renderCannonballShards(gc, sceneWidth, xScale, yScale);

		gc.setFont(new Font("Courier New Bold", 40));
		renderOffenciveCannonWords(gc, xScale, yScale);
		renderCannonballWords(gc, sceneWidth, xScale, yScale);

		renderHpBars(gc, sceneWidth, xScale, yScale);

		//Debug
/*
		gc.setStroke(new Color(0, 0, 0, 1));
		gc.setLineWidth(2);
		for (int i = 0; i < lines.size(); ++i)
			gc.strokeLine(xScale * lines.get(i).x1, yScale * lines.get(i).y1, xScale * lines.get(i).x2, yScale * lines.get(i).y2);
		gc.setFill(new Color(0, 1, 0, 1));
		gc.setLineWidth(1);
		for (int i = 0; i < points.size(); ++i)
			gc.fillOval(xScale * points.get(i).getX(), yScale * points.get(i).getY(), 3, 3);
		gc.setFill(new Color(1, 0, 0, 1));
		for (int i = 0; i < colPoints.size(); ++i)
			gc.fillOval(xScale * colPoints.get(i).getX(), yScale * colPoints.get(i).getY(), 5, 5);
*/
	}

	private void renderPlayerImage(GraphicsContext gc, Image image, PvpObject.Belonging belonging, double x, double y, double sceneWidth, double horizontalScale, double verticalScale)
	{
		double finalX, finalY, finalWidth, finalHeight;
		if (belonging == PvpObject.Belonging.FRIENDLY)
		{
			finalX = x * horizontalScale;
			finalWidth = image.getWidth() * horizontalScale;
		}
		else
		{
			finalX = sceneWidth - x * horizontalScale;
			finalWidth = -image.getWidth() * horizontalScale;
		}
		finalY = y * verticalScale;
		finalHeight = image.getHeight() * verticalScale;

		gc.drawImage(image, finalX, finalY, finalWidth, finalHeight);
	}

	private void renderPlayerText(GraphicsContext gc, String text, boolean isFillNeeded, boolean isStrokeNeeded, PvpObject.Belonging belonging, double x, double y, double maxWidth, double sceneWidth, double horizontalScale, double verticalScale)
	{
		double finalX, finalY, finalMaxWidth;
		if (belonging == PvpObject.Belonging.FRIENDLY)
		{
			finalX = x * horizontalScale;
		}
		else
		{
			finalX = sceneWidth - x * horizontalScale;
		}
		finalMaxWidth = maxWidth * horizontalScale;
		finalY = y * verticalScale;

		if (isFillNeeded)
			gc.fillText(text, finalX, finalY, finalMaxWidth);
		if (isStrokeNeeded)
			gc.strokeText(text, finalX, finalY, finalMaxWidth);
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
		isGameProceed = false;
		isGameProceed = false;
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
		shotMP = new MediaPlayer(new Media(new File("src/typingtrainer/GameScene/sounds/shot_" + (int) (1 + Math.random() * 3) + ".mp3").toURI().toString()));
		shotMP.play();
	}

	private void playBallsCollisionSound()
	{
		if (ballsCollisionMP != null)
		{
			MediaPlayer buf = ballsCollisionMP;
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
		ballsCollisionMP = new MediaPlayer(new Media(new File("src/typingtrainer/GameScene/sounds/balls_collision_" + (int) (1 + Math.random() * 3) + ".wav").toURI().toString()));
		ballsCollisionMP.play();
	}

	private void playShipDamagedSound()
	{
		if (shipHitMP != null)
		{
			MediaPlayer buf = shipHitMP;
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
		shipHitMP = new MediaPlayer(new Media(new File("src/typingtrainer/GameScene/sounds/ship_hit_" + (int) (1 + Math.random() * 6) + ".wav").toURI().toString()));
		shipHitMP.play();
	}

	public void setPlayerNames(String p1, String p2)
	{
		game.getShip(0).setPlayerName(p1);
		game.getShip(1).setPlayerName(p2);
	}
}
