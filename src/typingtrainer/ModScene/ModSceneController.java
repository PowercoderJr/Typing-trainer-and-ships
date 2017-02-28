package typingtrainer.ModScene;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import typingtrainer.Main;
import typingtrainer.ManagedScene;
import typingtrainer.PracticeScene.PracticeSceneController;
import typingtrainer.SceneManager;

import java.io.IOException;

/**
 * Created by Никитка on 28.02.2017.
 */
public class ModSceneController {

    public ChoiceBox lang;
    public ChoiceBox difficulty;
    public ChoiceBox register;

    public void initialize()
    {
        System.out.println("Опционная сцена готова!");
        lang.setItems(FXCollections.observableArrayList("Русский", "English"));
        difficulty.setItems(FXCollections.observableArrayList("2", "4","6", "8","10", "12","14", "16","18", "20","22", "24","26", "28","30", "32", "33"));
        register.setItems(FXCollections.observableArrayList("Вкл.", "Выкл."));
    }

    public void onGoClicked(MouseEvent mouseEvent) throws IOException {

        if ( lang.getSelectionModel().isEmpty() || difficulty.getSelectionModel().isEmpty() || register.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ага, конечно");
            alert.setHeaderText(null);
            alert.setContentText("Воу, не так быстро, дружочек");
            alert.showAndWait();
        }
        else {
            PracticeSceneController.setOptions(lang.getSelectionModel().getSelectedIndex(),difficulty.getSelectionModel().getSelectedIndex(),register.getSelectionModel().getSelectedIndex());

            SceneManager sceneManager = ((ManagedScene)(((Label)mouseEvent.getSource()).getScene())).getManager();
            Parent practiceSceneFXML = FXMLLoader.load(Main.class.getResource("PracticeScene/practiceScene.fxml"));
            ManagedScene practiceScene = new ManagedScene(practiceSceneFXML, 1280, 720, sceneManager);
            practiceScene.getStylesheets().add("typingtrainer/PracticeScene/style.css");
            sceneManager.pushScene(practiceScene);
        }


    }
}
