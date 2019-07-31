package result;

import app.AppConfig;
import app.Bundles;
import entity.Student;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import view.FXMLController;

import java.io.IOException;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static result.DeptYearSearchResult.MosaicResult;

@FXMLController("fxml/deptYearCell.fxml")
public class DeptYearResultCell extends ListCell<MosaicResult> {

    @FXML private Label departement;
    @FXML private Label year;
    @FXML private GridPane gridPane;

    private ResourceBundle resources;

    private double maxWidth;

    public DeptYearResultCell(double maxWidth) {
        this.maxWidth = maxWidth;
        loadFXML();
    }

    private void loadFXML() {
        resources = ResourceBundle.getBundle(Bundles.STRINGS.bundle(), AppConfig.getInstance().getLocale());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/deptYearCell.fxml"));
            loader.setResources(resources);
            loader.setController(this);
            loader.setRoot(this);

            loader.load();
            setPadding(new Insets(20,0, 20,10));
            gridPane.prefWidthProperty().setValue(maxWidth);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void updateItem(MosaicResult result, boolean empty) {
        super.updateItem(result, empty);

        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            departement.setText(result.departement.displayName);
            year.setText(result.year);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        }


    }

}
