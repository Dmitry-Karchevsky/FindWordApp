package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Controller {

    private ObservableList<WordCount> data;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea textInFile;

    @FXML
    private TextField filePath;

    @FXML
    private Button buttonRead;

    @FXML
    private TableView<WordCount> tableWordCount;

    @FXML
    private TableColumn<WordCount, String> wordColumn;

    @FXML
    private TableColumn<WordCount, Integer> countColumn;

    @FXML
    void initialize() {
        textInFile.setEditable(false);
        tableWordCount.setEditable(false);
        wordColumn.setCellValueFactory(new PropertyValueFactory<>("word"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        textInFile.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
            tableWordCount.getSelectionModel().clearSelection();
            data.stream().filter(x -> x.getWord().equals(textInFile.selectedTextProperty().getValue())).findAny()
                    .ifPresent(wordCount -> tableWordCount.getSelectionModel().select(wordCount));
        });

    }

    @FXML
    void onClickButton() {
        if (!filePath.getText().isEmpty()){
            textInFile.clear();
            try {
                String text = new String(Files.readAllBytes(Paths.get(filePath.getText())));
                List<WordCount> listWordCounts = Arrays.stream(text.split("[\\s., \n]+"))
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .entrySet().stream().map(pair -> new WordCount(pair.getKey(), pair.getValue()))
                        .collect(Collectors.toList());
                data = FXCollections.observableArrayList(listWordCounts);
                tableWordCount.setItems(data);
                textInFile.appendText(text);
            } catch (NoSuchFileException exception) {
                filePath.clear();
                filePath.appendText("File Not Found");
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
