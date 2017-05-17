package typingtrainer.GameScene;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
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
import typingtrainer.Game.*;
import typingtrainer.InfoScene.InfoSceneController;
import typingtrainer.Main;
import typingtrainer.ManagedScene;
import typingtrainer.PregameServerScene.PregameServerSceneController;
import typingtrainer.SceneManager;
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

	private static final int dt = 15;
	public static final int BACKGROUND_STEP = 2;
	public static final double BACKGROUND_SPEED = BACKGROUND_STEP * 1000.0 / dt;

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
	private Image spaceImg;
	private Image enterImg;

	private double pregameTimer;
	private boolean isSceneClosed;
	private boolean isEscPreccedOnce;
	private Game game;

	private EventHandler<KeyEvent> onKeyPressed = new EventHandler<KeyEvent>()
	{
		@Override
		public void handle(KeyEvent event)
		{
			if (event.getCode() == KeyCode.ESCAPE)
			{
				if (isEscPreccedOnce)
				{
					try
					{
						ostream.writeUTF(DISCONNECT_CODEGRAM + ":");
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					Platform.runLater(() -> leave());
				}
				else
				{
					isEscPreccedOnce = true;
					new Thread(() ->
					{
						try
						{
							Thread.sleep(3000);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						isEscPreccedOnce = false;
					}).start();
				}
			}
			else if (game.isGameProceed())
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
		return new Point2D(Main.DEFAULT_SCREEN_WIDTH - point.getX(), point.getY());
	}

	public static double mirrorRelativelyToDefaultWidth(double x)
	{
		return Main.DEFAULT_SCREEN_WIDTH - x;
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
		Canvas canvas = new Canvas(Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT);
		canvas.setOnKeyPressed(onKeyPressed);
		canvas.setFocusTraversable(true);
		root.getChildren().add(canvas);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(1.5);

		bgImg = new Image("typingtrainer/GameScene/sea_background.png");
		bg1Y = 0.0;
		bg2Y = 0.0;
		game = new Game(lang, difficulty, isRegister);
		isSceneClosed = false;
		isEscPreccedOnce = false;
		hpBarBackground = new WritableImage(Game.SPRITE_SHEET.getPixelReader(), 132, 349, 255, 26);
		spaceImg = new WritableImage(Game.SPRITE_SHEET.getPixelReader(), 148, 55, 70, 11);
		enterImg = new WritableImage(Game.SPRITE_SHEET.getPixelReader(), 218, 55, 16, 14);

		//Ожидание сообщений
		new Thread(this::waitForMessages).start();

		//Игра
		new Thread(() ->
		{
			//Задержка перед стартом
			try
			{
				pregameTimer = 3.0;
				do
				{
					Platform.runLater(() ->
					{
						render(gc);
						renderBroadcastingMessage(gc, "" + (int) Math.ceil(pregameTimer), 200, scene.getHeight() / 2, scene.getWidth(), scene.getHeight());
					});
					Thread.sleep(dt);
					pregameTimer -= dt / 1000.0;
				} while (pregameTimer > 0);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			if (!isSceneClosed)
			{
				game.setGameProceed(true);

				//Интерфейс, логика
				new Thread(() ->
				{
					try
					{
						while (game.isGameProceed())
						{
							game.tick(dt);
							Platform.runLater(() -> render(gc));
							Thread.sleep(dt);
						}

						final String winner = (game.getShip(1).getHp() < 0.001 ? game.getShip(0).getPlayerName() : game.getShip(1).getPlayerName()) + " победил!";
						while (!isSceneClosed)
						{
							game.tick(dt);
							Platform.runLater(() ->
							{
								render(gc);
								renderBroadcastingMessage(gc, winner, 100, scene.getHeight() / 2, scene.getWidth(), scene.getHeight());
							});
							Thread.sleep(dt);
						}
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}

				}).start();

				//Звуки столкновения
				new Thread(() ->
				{
					try
					{
						while (!isSceneClosed)
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
			}
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
					Platform.runLater(() ->
					{
						leave();
						try
						{
							InfoSceneController.setInfo("Соперник отключился");
							SceneManager sceneManager = scene.getManager();
							Parent infoSceneFXML = FXMLLoader.load(Main.class.getResource("InfoScene/infoScene.fxml"));
							ManagedScene infoScene = new ManagedScene(infoSceneFXML, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT, sceneManager);
							infoScene.getStylesheets().add("typingtrainer/infoScene/style.css");
							sceneManager.pushScene(infoScene);
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
					});
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
		System.out.println("Ждём сообщений");
		try
		{
			DataInputStream istream = new DataInputStream(socket.getInputStream());
			while (!isSceneClosed)
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
			System.out.println(e.getMessage());
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
		bg1Y += BACKGROUND_STEP;
		bg2Y = bg1Y - bgSize + BACKGROUND_STEP * 2;
		gc.drawImage(bgImg, 0, bg1Y, bgSize, bgSize);
		gc.drawImage(bgImg, 0, bg2Y, bgSize, bgSize);
	}

	private void renderShips(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		for (int i = 0; i < Game.SHIPS_COUNT; ++i)
		{
			Ship ship = game.getShip(i);
			ship.render(gc, sceneWidth, xScale, yScale);

			//Cannons
			ship.getDefenciveCannon().render(gc, sceneWidth, xScale, yScale);
			for (int j = 0; j < Ship.OFFENCIVE_CANNONS_COUNT; ++j)
			{
				OffenciveCannon cannon = ship.getOffenciveCannon(j);
				cannon.render(gc, sceneWidth, xScale, yScale);
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

	private void renderBroadcastingMessage(GraphicsContext gc, String text, int fontSize, double y, double sceneWidth, double sceneHeight)
	{
		gc.setFont(new Font("Arial Bold", fontSize));
		gc.setFill(Color.WHITE);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.fillText(text, sceneWidth / 2, y, Main.DEFAULT_SCREEN_WIDTH - 100);
		gc.strokeText(text, sceneWidth / 2, y, Main.DEFAULT_SCREEN_WIDTH - 100);
	}

	private void renderCannonballs(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		for (int i = 0; i < game.getCannonballs().size(); ++i)
		{
			game.getCannonballs().get(i).render(gc, sceneWidth, xScale, yScale);
			//Debug
			points.add(new Point2D(game.getCannonballs().get(i).getBelonging() == PvpObject.Belonging.HOSTILE ? Main.DEFAULT_SCREEN_WIDTH - game.getCannonballs().get(i).getPosition().getX() : game.getCannonballs().get(i).getPosition().getX(), game.getCannonballs().get(i).getPosition().getY()));
			//
		}
	}

	private void renderSmokeClouds(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		for (int i = 0; i < game.getSmokeClouds().size(); ++i)
			game.getSmokeClouds().get(i).render(gc, sceneWidth, xScale, yScale);
	}

	private void renderCannonballShards(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		for (int i = 0; i < game.getCannonballShards().size(); ++i)
			game.getCannonballShards().get(i).render(gc, sceneWidth, xScale, yScale);
	}

	private void renderWoodenSplinters(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		for (int i = 0; i < game.getSplinterPiles().size(); ++i)
			for (int j = 0; j < WoodenSplintersPile.SPLINTERS_COUNT; ++j)
				game.getSplinterPiles().get(i).getSplinter(j).render(gc, sceneWidth, xScale, yScale);
	}

	private void renderOffenciveCannonWords(GraphicsContext gc, double sceneWidth, double xScale, double yScale)
	{
		gc.setTextAlign(TextAlignment.LEFT);
		for (int i = 0; i < Ship.OFFENCIVE_CANNONS_COUNT; ++i)
		{
			OffenciveCannon cannon = game.getShip(0).getOffenciveCannon(i);
			String substrBefore = cannon.getPvpWord().getSubstrBeforeWithSpaces(), substrAfter = cannon.getPvpWord().getSubstrAfterWithSpaces();
			double x = 10, y = Ship.CANNON_BASE_POSITIONS[i + 1].getY() + cannon.getImage().getHeight() + WORD_OFFSET_Y;
			gc.setFill(BEFORE_FILL_COLOR);
			gc.setStroke(BEFORE_STROKE_COLOR);
			renderPlayerText(gc, substrBefore, true, true, cannon.getBelonging(), x, y, 10000, sceneWidth, xScale, yScale);
			gc.setFill(AFTER_FILL_COLOR);
			gc.setStroke(AFTER_STROKE_COLOR);
			renderPlayerText(gc, substrAfter, true, true, cannon.getBelonging(), x, y, 10000, sceneWidth, xScale, yScale);

			//Иконка SPACE
			if (cannon.getPvpWord().getCharsDone() >= Game.MIN_WORD_LENGTH_TO_SHOOT)
			{
				x = cannon.getPosition().getX() + 68;
				y = cannon.getPosition().getY() + cannon.getPivot().getY() - spaceImg.getHeight() / 2;
				gc.drawImage(spaceImg, x * xScale, y * yScale, spaceImg.getWidth() * xScale, spaceImg.getHeight() * yScale);
			}
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
				double x = cannonball.getPosition().getX() + cannonball.getPivot().getX();
				double y = cannonball.getPosition().getY() + cannonball.getImage().getHeight() * cannonball.getScale() + WORD_OFFSET_Y;
				//Debug
				//gc.fillOval(sceneWidth - x * xScale - 2.5, (y - cannonball.getPivot().getY() - WORD_OFFSET_Y) * yScale - 2.5, 5, 5);
				//
				gc.setFill(BEFORE_FILL_COLOR);
				gc.setStroke(BEFORE_STROKE_COLOR);
				renderPlayerText(gc, substrBefore, true, true, cannonball.getBelonging(), x, y, 10000, sceneWidth, xScale, yScale);
				gc.setFill(AFTER_FILL_COLOR);
				gc.setStroke(AFTER_STROKE_COLOR);
				renderPlayerText(gc, substrAfter, true, true, cannonball.getBelonging(), x, y, 10000, sceneWidth, xScale, yScale);

				//Иконка ENTER
				if (cannonball.getPvpWord().getCharsDone() == cannonball.getPvpWord().toString().length())
				{
					x = Main.DEFAULT_SCREEN_WIDTH - cannonball.getPosition().getX() + 4;
					y = cannonball.getPosition().getY() + cannonball.getPivot().getY() - enterImg.getHeight() / 2;
					gc.drawImage(enterImg, x * xScale, y * yScale, enterImg.getWidth() * xScale, enterImg.getHeight() * yScale);
				}
			}
		}
	}

	private void render(GraphicsContext gc)
	{
		double sceneWidth = scene.getWidth();
		double sceneHeight = scene.getHeight();

		//Scaling
		gc.getCanvas().setWidth(sceneWidth);
		gc.getCanvas().setHeight(sceneHeight);
		double xScale = sceneWidth / Main.DEFAULT_SCREEN_WIDTH;
		double yScale = sceneHeight / Main.DEFAULT_SCREEN_HEIGHT;
		double bgSize = Main.DEFAULT_SCREEN_WIDTH * Math.max(xScale, yScale);

		renderBackground(gc, sceneHeight, bgSize);
		renderWoodenSplinters(gc, sceneWidth, xScale, yScale);
		renderCannonballs(gc, sceneWidth, xScale, yScale);
		renderShips(gc, sceneWidth, xScale, yScale);
		renderSmokeClouds(gc, sceneWidth, xScale, yScale);
		renderCannonballShards(gc, sceneWidth, xScale, yScale);
		renderHpBars(gc, sceneWidth, xScale, yScale);

		if (game.isGameProceed())
		{
			gc.setFont(new Font("Courier New Bold", 40));
			renderOffenciveCannonWords(gc, sceneWidth, xScale, yScale);
			renderCannonballWords(gc, sceneWidth, xScale, yScale);
		}

		if (isEscPreccedOnce)
		{
			renderBroadcastingMessage(gc, "Для выхода нажмите ESC ещё раз", 30, 70, sceneWidth, sceneHeight);
		}

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

	private void leave()
	{
		game.setGameProceed(false);
		isSceneClosed = true;
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			scene.getManager().popScene();
			scene.getManager().popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
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
