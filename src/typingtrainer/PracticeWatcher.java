package typingtrainer;

/**
 * Created by Meow on 26.02.2017.
 */
public class PracticeWatcher
{
	public static final int DISPLAYABLE_SPACE_SIZE = 10;
	private StringBuffer taskString;
	private int mistakeCount;
	private long timeStart;

	public PracticeWatcher(StringBuffer taskString)
	{
		this.taskString = taskString;
		mistakeCount = 0;
		timeStart = System.nanoTime();
	}

	public char GetCurrentChar()
	{
		return taskString.charAt(0);
	}

	public void PassCurrentChar()
	{
		taskString.deleteCharAt(0);
	}

	public String GetDisplayableString()
	{
		return taskString.substring(0, DISPLAYABLE_SPACE_SIZE);
	}
}
