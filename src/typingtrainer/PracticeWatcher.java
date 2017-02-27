package typingtrainer;

/**
 * Created by Meow on 26.02.2017.
 */
public class PracticeWatcher
{
	public int DISPLAYABLE_SPACE_SIZE = 10;
	private StringBuffer taskString;
	private int mistakeCount;
	private long timeStart;

	public PracticeWatcher(StringBuffer taskString)
	{
		this.taskString = taskString;
		mistakeCount = 0;
		timeStart = System.nanoTime();
		if (taskString.length()>=10)
		{
			this.DISPLAYABLE_SPACE_SIZE = 10;
		}
		else
		{
			if (taskString.length()>=5)
			{
				this.DISPLAYABLE_SPACE_SIZE = 5;
			}
			else
			{
				this.DISPLAYABLE_SPACE_SIZE = 1;
			}

		}
	}

	public char GetCurrentChar()
	{
		return taskString.charAt(0);
	}

	public void PassCurrentChar()
	{
		taskString.deleteCharAt(0);
	}
/* временно
	public String GetDisplayableString()
	{
		return taskString.substring(0, DISPLAYABLE_SPACE_SIZE);
	}
*/
	public String GetDisplayableString()
	{
		return taskString.toString();
	}

	public void AddMistake()
	{
		this.mistakeCount++;
	}

	public int getMistakeCount() {
		return mistakeCount;
	}

	public long GetFinalTime(){
		return  System.nanoTime() - timeStart;
	}
}
