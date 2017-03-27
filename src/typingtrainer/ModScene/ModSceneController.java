package typingtrainer.ModScene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import typingtrainer.Main;
import typingtrainer.ManagedScene;
import typingtrainer.PracticeScene.PracticeSceneController;
import typingtrainer.SceneManager;
import typingtrainer.Word;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Created by Никитка on 28.02.2017.
 */
public class ModSceneController {

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

    public void initialize() throws IOException {
		System.out.println("Опционная сцена готова!");
		ObservableList<String> levels = FXCollections.observableArrayList();
		for (int i = 0; i < 30; i += 2)
			levels.add(new String() + (i + 2) + " (" + Word.ALPH_RU[0].charAt(i) + Word.ALPH_RU[0].charAt(i + 1) +
				" / " + Word.ALPH_EN[0].charAt(i) + Word.ALPH_EN[0].charAt(i + 1) + ")");
		levels.add("33" + " (" + Word.ALPH_RU[0].charAt(30) + Word.ALPH_RU[0].charAt(31) + Word.ALPH_RU[0].charAt(32) +
			" / " + Word.ALPH_EN[0].charAt(30) + Word.ALPH_EN[0].charAt(31) + Word.ALPH_EN[0].charAt(32) + ")");
        langCB.setItems(FXCollections.observableArrayList("Русский", "English"));
        difficultyCB.setItems(levels);



		try {
			FileReader settings_read = new FileReader("src/typingtrainer/ModScene/Settings/settings.txt");
			BufferedReader reader = new BufferedReader(settings_read);
			String ln;
			ArrayList<String> lns = new ArrayList<String>();
			while ((ln = reader.readLine()) != null) {
				lns.add(ln);
			}
			reader.close();
			settings_read.close();

			if (!lns.isEmpty()) {

				int language = 0;
				int difficulty = 0;
				boolean register = false;
				boolean music = false;
				boolean sound = false;

				language = Integer.valueOf(lns.get(0)) == 0 ? 0 : 1;
				difficulty = Integer.valueOf(lns.get(1));
				register = Integer.valueOf(lns.get(2)) == 0 ? false : true;
				music = Integer.valueOf(lns.get(3)) == 0 ? false : true;
				sound = Integer.valueOf(lns.get(4)) == 0 ? false : true;

				langCB.getSelectionModel().select(language);
				difficultyCB.getSelectionModel().select(difficulty);
				registerChb.setSelected(register);
				musicChb.setSelected(music);
				soundsChb.setSelected(sound);

			} else {
				langCB.getSelectionModel().select(0);
				difficultyCB.getSelectionModel().select(0);
				registerChb.setSelected(false);
				musicChb.setSelected(true);
				soundsChb.setSelected(true);
			}
		}catch (Exception e){
			System.out.println("Файл статистики отсутствует");
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Ошибка");
			alert.setHeaderText(null);
			alert.setContentText("Файл статистики отсутствует");
			alert.showAndWait();


			langCB.getSelectionModel().select(0);
			difficultyCB.getSelectionModel().select(0);
			registerChb.setSelected(false);
			musicChb.setSelected(true);
			soundsChb.setSelected(true);
		}
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

			FileWriter settings_wr = new FileWriter("src/typingtrainer/ModScene/Settings/settings.txt");

			settings_wr.write(lng + "\r\n" +  diff + "\r\n" +	reg + "\r\n" +	 mus + "\r\n" + snd);
			settings_wr.flush();
			settings_wr.close();


            PracticeSceneController.setOptions(lang, difficulty, registerChb.isSelected(), musicChb.isSelected(), soundsChb.isSelected());

            SceneManager sceneManager = ((ManagedScene)(((Label)mouseEvent.getSource()).getScene())).getManager();
            Parent practiceSceneFXML = FXMLLoader.load(Main.class.getResource("PracticeScene/practiceScene.fxml"));
            ManagedScene practiceScene = new ManagedScene(practiceSceneFXML, 1280, 720, sceneManager);
            practiceScene.getStylesheets().add("typingtrainer/PracticeScene/style.css");
            sceneManager.pushScene(practiceScene);
        }
    }

	public void onBackClicked(MouseEvent mouseEvent)
	{
		try
		{
			((ManagedScene)(((Label)mouseEvent.getSource()).getScene())).getManager().popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
