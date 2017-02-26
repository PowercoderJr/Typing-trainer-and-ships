package typingtrainer;

import javafx.scene.Parent;
import javafx.scene.Scene;
import typingtrainer.SceneManager;

/**
 * Created by Meow on 25.02.2017.
 * Расширяет класс Scene из JavaFX. Отличается только тем, что имеет ссылку на свой менеджер.
 * @see SceneManager
 */
public class ManagedScene extends Scene
{
	private SceneManager manager;

	public ManagedScene(Parent root, SceneManager manager)
	{
		super(root);
		this.manager = manager;
	}

	public ManagedScene(Parent root, double width, double height, SceneManager manager)
	{
		super(root, width, height);
		this.manager = manager;
	}

	public SceneManager getManager()
	{
		return manager;
	}
}
