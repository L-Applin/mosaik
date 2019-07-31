package result;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import static result.StudentSearchResult.MosaicResult;

public class StudentResultCellFactory implements Callback<ListView<MosaicResult>, ListCell<MosaicResult>> {

    private double maxWidth;

    public StudentResultCellFactory(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public ListCell<MosaicResult> call(ListView<MosaicResult> param) {
        return new StudentResultCell(maxWidth);
    }

}
