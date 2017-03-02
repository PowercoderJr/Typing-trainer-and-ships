package typingtrainer;

import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

/**
 * Created by Meow on 25.02.2017.
 * Содержит стек scenes - история сцен. Благодаря ему из какой-либо сцены можно вернуться на главную.
 */
public class SceneManager
{
	private Stage stage;
	private Stack<ManagedScene> scenes;

	public SceneManager(Stage stage)
	{
		this.stage = stage;
		scenes = new Stack<>();
	}

	public void pushScene(ManagedScene scene)
	/** Помещает новую сцену в стек и переключает на неё stage
	 * @param scene - Новая сцена.
	 */
	{
		scenes.push(scene);
		double w = stage.getWidth(), h = stage.getHeight();
		stage.setScene(scene);
		stage.setWidth(w);
		stage.setHeight(h);
	}

	public void popScene() throws InvocationTargetException
	{
		/** Извлекает последнюю сцнеу из стека и переключает stage на предыдущую. Если последняя сцена была единственной, закрывает stage.
		 * @throws InvocationTargetException если стек был пуст.
		 */
		if (scenes.empty())
			throw new InvocationTargetException(new Throwable(), "Стек сцен пуст!");
		scenes.pop();
		if (scenes.empty())
			stage.close();
		else
		{
			double w = stage.getWidth(), h = stage.getHeight();
			stage.setScene(scenes.peek());
			stage.setWidth(w);
			stage.setHeight(h);
		}
	}

	public void popAllExceptFirst() throws InvocationTargetException
	{
		/** Извлекает из стека все сцены, кроме первой (которая была установлена в конструкторе) и переключает на неё stage.
		 * @throws InvocationTargetException если стек был пуст.
 		 */
		if (scenes.empty())
			throw new InvocationTargetException(new Throwable(), "Стек сцен пуст!");
		double w = stage.getWidth(), h = stage.getHeight();
		while (scenes.size() > 1)
			scenes.pop();
		stage.setScene(scenes.peek());
		stage.setWidth(w);
		stage.setHeight(h);
	}

	public ManagedScene getAt(int index)
	{
		return scenes.get(index);
	}

	public int getStackSize()
	{
		return scenes.size();
	}

	public ManagedScene getCurrScene()
	{
		return scenes.peek();
	}
}
