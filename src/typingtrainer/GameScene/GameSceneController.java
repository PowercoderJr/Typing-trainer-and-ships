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

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Meow on 22.04.2017.
 */
public class GameSceneController
{
	private static final int DEFAULT_SCREEN_WIDTH = 1280;
	private static final int DEFAULT_SCREEN_HEIGHT = 720;
	private static final int dt = 15;
	private static final int BACKGROUND_SPEED = 2;

	private ManagedScene scene;
	private Image bg1img, bg2img;
	private double bg1Y, bg2Y;

	private boolean isRendering;

	private EventHandler<KeyEvent> onKeyPressed = new EventHandler<KeyEvent>()
	{
		@Override
		public void handle(KeyEvent event)
		{
			if (event.getCode() == KeyCode.ESCAPE)
			{
				isRendering = false;
				try
				{
					scene.getManager().popScene();
				}
				catch (InvocationTargetException e)
				{
					System.out.println(e.getMessage());
				}
			}
		}
	};

	/*@FXML
	public Pane pane;
	@FXML
	public ImageView bg1ImageView;
	@FXML
	public ImageView bg2ImageView;*/

	private Game game;

	/*public void initialize() throws InterruptedException
	{
		System.out.println("Игровая сцена готова!");

		game = new Game();
		Ship ship = game.getShip(0);
		pane.getChildren().add(ship.getImageView());
		ship.getImageView().setX(0);
		ship.getImageView().setY(0);

		new Thread(() ->
		{
			try
			{
				while (true)
				{
					updateModel();
					updateGUI();
					Thread.sleep(dt);
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}).start();
	}*/

	public GameSceneController(ManagedScene scene)
	{
		System.out.println("Игровая сцена готова!");
		this.scene = scene;
		Group root = (Group) scene.getRoot();
		Canvas canvas = new Canvas(DEFAULT_SCREEN_WIDTH, DEFAULT_SCREEN_HEIGHT);
		//canvas.addEventHandler(KeyEvent.KEY_PRESSED, onKeyPressed);
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

	/*private void updateGUI()
	{
		//GraphicsContext?

		//Scaling
		double xScale = pane.getWidth() / DEFAULT_SCREEN_WIDTH;
		double yScale = pane.getHeight() / DEFAULT_SCREEN_HEIGHT;
		double bgSize = DEFAULT_SCREEN_WIDTH * Math.max(xScale, yScale);
		bg1ImageView.setFitWidth(bgSize);
		bg1ImageView.setFitHeight(bgSize);
		bg2ImageView.setFitWidth(bgSize);
		bg2ImageView.setFitHeight(bgSize);

		//Moving background
		if (bg1ImageView.getY() >= pane.getHeight())
		{
			ImageView buf = bg1ImageView;
			bg1ImageView = bg2ImageView;
			bg2ImageView = buf;
		}
		bg1ImageView.setY(bg1ImageView.getY() + BACKGROUND_SPEED);
		bg2ImageView.setY(bg1ImageView.getY() - bg2ImageView.getFitHeight() + BACKGROUND_SPEED);

		//Drawing ships
	}*/
}
