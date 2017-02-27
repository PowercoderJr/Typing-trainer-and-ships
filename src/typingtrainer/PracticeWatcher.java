package typingtrainer;

/**
 * Created by Meow on 26.02.2017.
 */
public class PracticeWatcher
{
	public static final int DISPLAYABLE_SPACE_SIZE = 20;
	private StringBuffer taskString;
	private Word.Languages lang;
	private int mistakeCount;
	private long timeStart;

	public PracticeWatcher(StringBuffer taskString, Word.Languages lang)
	{
		this.taskString = taskString;
		this.lang = lang;
		mistakeCount = 0;
		timeStart = System.nanoTime();
	}

	public char getCurrentChar()
	{
		return taskString.charAt(0);
	}

	public void passCurrentChar()
	{
		taskString.deleteCharAt(0);
	}

	public String getDisplayableString()
	{
		return taskString.substring(0, taskString.length() >= DISPLAYABLE_SPACE_SIZE ? DISPLAYABLE_SPACE_SIZE : taskString.length());
	}

	public void addMistake()
	{
		this.mistakeCount++;
	}

	public int getMistakeCount() {
		return mistakeCount;
	}

	public long getFinalTime(){
		return  System.nanoTime() - timeStart;
	}

	public Word.Languages getLang()
	{
		return lang;
	}
}
