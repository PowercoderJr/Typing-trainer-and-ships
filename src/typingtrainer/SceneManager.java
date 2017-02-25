package typingtrainer;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

/**
 * Created by Meow on 25.02.2017.
 */
public class SceneManager
{
	private Stage stage;
	private Stack<Scene> scenes;

	public SceneManager(Stage stage)
	{
		this.stage = stage;
		scenes = new Stack<>();
	}

	public void pushScene(Scene scene)
	{
		scenes.push(scene);
		stage.setScene(scene);
	}

	public void popScene()
	{
		/*if (scenes.empty())
			throw */
		scenes.pop();
		stage.setScene(scenes.peek());
	}
}
