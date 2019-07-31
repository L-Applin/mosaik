package result;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import static result.DeptYearSearchResult.MosaicResult;

public class DeptYearResultCellFactory implements Callback<ListView<MosaicResult>, ListCell<MosaicResult>> {

    private double maxWidth;

    public DeptYearResultCellFactory(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public ListCell<MosaicResult> call(ListView<MosaicResult> param) {
        return new DeptYearResultCell(maxWidth);
    }

}
