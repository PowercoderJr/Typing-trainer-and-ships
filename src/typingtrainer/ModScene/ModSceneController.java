package typingtrainer.ModScene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import typingtrainer.Main;
import typingtrainer.ManagedScene;
import typingtrainer.PracticeScene.PracticeSceneController;
import typingtrainer.Word;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Никитка on 28.02.2017.
 */
public class ModSceneController
{
	@FXML
	public ChoiceBox langCB;
	@FXML
	public ChoiceBox difficultyCB;
	@FXML
	public CheckBox registerChb;
	@FXML
	public CheckBox musicChb;
	@FXML
	public CheckBox soundsChb;

	public void initialize()
	{
		System.out.println("Опционная сцена готова!");
		ObservableList<String> levels = FXCollections.observableArrayList();
		for (int i = 0; i < 30; i += 2)
			levels.add(new String() + (i + 2) + " (" + Word.ALPH_RU[0].charAt(i) + Word.ALPH_RU[0].charAt(i + 1) +
					" / " + Word.ALPH_EN[0].charAt(i) + Word.ALPH_EN[0].charAt(i + 1) + ")");
		levels.add("33" + " (" + Word.ALPH_RU[0].charAt(30) + Word.ALPH_RU[0].charAt(31) + Word.ALPH_RU[0].charAt(32) +
				" / " + Word.ALPH_EN[0].charAt(30) + Word.ALPH_EN[0].charAt(31) + Word.ALPH_EN[0].charAt(32) + ")");
		langCB.setItems(FXCollections.observableArrayList("Русский", "English"));
		difficultyCB.setItems(levels);

		int language, difficulty;
		boolean register, music, sound;
		try (
				FileReader settings_read = new FileReader("settings.txt");
				BufferedReader reader = new BufferedReader(settings_read);)
		{
			language = Integer.valueOf(reader.readLine());
			difficulty = Integer.valueOf(reader.readLine());
			register = Integer.valueOf(reader.readLine()) == 1;
			music = Integer.valueOf(reader.readLine()) == 1;
			sound = Integer.valueOf(reader.readLine()) == 1;
		}
		catch (Exception e)
		{
			language = 0;
			difficulty = 0;
			register = false;
			music = true;
			sound = true;
		}
		langCB.getSelectionModel().select(language);
		difficultyCB.getSelectionModel().select(difficulty);
		registerChb.setSelected(register);
		musicChb.setSelected(music);
		soundsChb.setSelected(sound);
	}

	public void onGoClicked(MouseEvent mouseEvent) throws IOException
	{
		{
			Word.Languages lang;
			int difficulty;
			switch (langCB.getSelectionModel().getSelectedIndex())
			{
				case 0:
				default:
					lang = Word.Languages.RU;
					break;
				case 1:
					lang = Word.Languages.EN;
					break;
			}
			String diffStr = difficultyCB.getSelectionModel().getSelectedItem().toString();
			difficulty = Integer.parseInt(diffStr.substring(0, diffStr.indexOf(' ')));
			int lng = langCB.getSelectionModel().getSelectedIndex();
			int diff = difficultyCB.getSelectionModel().getSelectedIndex();
			int reg = registerChb.isSelected() ? 1 : 0;
			int mus = musicChb.isSelected() ? 1 : 0;
			int snd = soundsChb.isSelected() ? 1 : 0;

			try (FileWriter settings_wr = new FileWriter("settings.txt"))
			{
				settings_wr.write(lng + "\r\n" + diff + "\r\n" + reg + "\r\n" + mus + "\r\n" + snd);
				settings_wr.flush();
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}

			PracticeSceneController.setOptions(lang, difficulty, registerChb.isSelected(), musicChb.isSelected(), soundsChb.isSelected());

			Parent practiceSceneFXML = FXMLLoader.load(Main.class.getResource("PracticeScene/practiceScene.fxml"));
			ManagedScene practiceScene = new ManagedScene(practiceSceneFXML, Main.DEFAULT_SCREEN_WIDTH, Main.DEFAULT_SCREEN_HEIGHT, Main.sceneManager);
			practiceScene.getStylesheets().add("typingtrainer/PracticeScene/style.css");
			Main.sceneManager.pushScene(practiceScene);

		}
	}

	public void onBackClicked(MouseEvent mouseEvent)
	{
		try
		{
			Main.sceneManager.popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
