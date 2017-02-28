package typingtrainer.ModScene;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import typingtrainer.Main;
import typingtrainer.ManagedScene;
import typingtrainer.PracticeScene.PracticeSceneController;
import typingtrainer.SceneManager;
import typingtrainer.Word;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Никитка on 28.02.2017.
 */
public class ModSceneController {

    public ChoiceBox langCB;
    public ChoiceBox difficultyCB;
    public ChoiceBox registerCB;

    public void initialize()
    {
        System.out.println("Опционная сцена готова!");
        langCB.setItems(FXCollections.observableArrayList("Русский", "English"));
        difficultyCB.setItems(FXCollections.observableArrayList("2", "4", "6", "8", "10", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "33"));
        registerCB.setItems(FXCollections.observableArrayList("Выкл.", "Вкл."));
    }

    public void onGoClicked(MouseEvent mouseEvent) throws IOException
	{
        if (langCB.getSelectionModel().isEmpty() || difficultyCB.getSelectionModel().isEmpty() || registerCB.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ага, конечно");
            alert.setHeaderText(null);
            alert.setContentText("Воу, не так быстро, дружочек");
            alert.showAndWait();
        }
        else
		{
			Word.Languages lang;
			int difficulty;
			boolean register;
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
			difficulty = Integer.parseInt(difficultyCB.getSelectionModel().getSelectedItem().toString());
			register = registerCB.getSelectionModel().getSelectedIndex() == 1;
            PracticeSceneController.setOptions(lang, difficulty, register);

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
