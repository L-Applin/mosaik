package view;

import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import result.ResultItem;
import result.StudentResultCellFactory;
import result.StudentSearchResult;

import java.util.Comparator;
import java.util.List;

import static result.StudentSearchResult.MosaicResult;

@FXMLController("fxml/studentResultDialog.fxml")
public class StudentResultDialogController {

    public AnchorPane mainPane;
    @FXML ListView<MosaicResult> resultList;

    public void initialize(){
        resultList.setFocusTraversable(false);
        resultList.setCellFactory(new StudentResultCellFactory(mainPane.getMaxWidth()));
        resultList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    System.out.println(newValue);
                });

        resultList.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaX() != 0) {
                event.consume();
            }
        });

    }

    public <T extends ResultItem> void addResults(List<T> items){
        items.forEach(res -> resultList.getItems().add((StudentSearchResult.MosaicResult) res));
        resultList.setItems(new SortedList<>(resultList.getItems(), Comparator.comparing(
                (MosaicResult res) -> res.student.getLastName(),
                String.CASE_INSENSITIVE_ORDER
        )));
    }


    public void overridePaneDimension(double width, double height){
        mainPane.prefWidthProperty().setValue(width);
        mainPane.prefHeightProperty().setValue(height);
    }

}
