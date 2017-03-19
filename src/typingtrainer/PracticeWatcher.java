package typingtrainer;

/**
 * Created by Meow on 26.02.2017.
 */
public class PracticeWatcher
{
	public static final int DISPLAYABLE_SPACE_SIZE = 20;
	private StringBuffer taskString;
	private Word.Languages lang;
	private long timeStart;
	private int initStringLength;
	private int mistakeCount;
	private int difficulty;
	private boolean register;

	public PracticeWatcher(StringBuffer taskString, Word.Languages lang, int difficulty, boolean register)
	{
		this.taskString = taskString;
		this.lang = lang;
		this.difficulty = difficulty;
		this.register = register;
		initStringLength = taskString.length();
		mistakeCount = 0;
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

	public int getDifficulty()
	{
		return difficulty;
	}

	public void setDifficulty(int difficulty)
	{
		this.difficulty = difficulty;
	}

	public boolean isRegister()
	{
		return register;
	}

	public void setRegister(boolean register)
	{
		this.register = register;
	}

	public int getInitStringLength()
	{
		return initStringLength;
	}

	public void rememberTimeStart()
	{
		timeStart = System.nanoTime();
	}
}
