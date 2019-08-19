package view;

import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import result.DeptYearResultCellFactory;
import result.ResultItem;

import java.util.Comparator;
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
        resultList.setItems(new SortedList<>(resultList.getItems(),
                Comparator.comparing (
                        (MosaicResult res) -> res.year,
                        String.CASE_INSENSITIVE_ORDER.reversed())
        ));
    }

    public void overridePaneDimension(double width, double height){
        mainPane.prefWidthProperty().setValue(width);
        mainPane.prefHeightProperty().setValue(height);
    }

}
