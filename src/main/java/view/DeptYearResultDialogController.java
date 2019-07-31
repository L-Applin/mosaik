package view;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import result.*;

import java.util.List;

import static result.DeptYearSearchResult.MosaicResult;

public class DeptYearResultDialogController {
    @FXML public AnchorPane mainPane;
    @FXML ListView<MosaicResult> resultList;

    public void initialize(){
        resultList.setFocusTraversable(false);
        resultList.setCellFactory(new DeptYearResultCellFactory(mainPane.getMaxWidth()));
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
        items.forEach(res -> resultList.getItems().add((MosaicResult) res));
    }

    public void overridePaneDimension(double width, double height){
        mainPane.prefWidthProperty().setValue(width);
        mainPane.prefHeightProperty().setValue(height);
    }

}
