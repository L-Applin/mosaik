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

import static result.StudentSearchResult.MosaicResult;

@FXMLController("fxml/studentCell.fxml")
public class StudentResultCell extends ListCell<MosaicResult> {

    @FXML private Label studentName;
    @FXML private Label studentGraduation;
    @FXML private Label studentDepartementYear;
    @FXML private GridPane gridPane;

    private double maxWidth;

    private ResourceBundle resources;

    public StudentResultCell(double maxWidth) {
        this.maxWidth=maxWidth;
        loadFXML();
    }

    private void loadFXML() {
        resources = ResourceBundle.getBundle(Bundles.STRINGS.bundle(), AppConfig.getInstance().getLocale());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/studentCell.fxml"));
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
    protected void updateItem(MosaicResult item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            Student student = item.student;
            studentName.setText(student.getFirstName() + " " + student.getLastName());
            ChronoLocalDate grad = student.getGraduationYear();
            studentGraduation.setText(grad.format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));
            studentDepartementYear.setText(student.getDepartement().displayName + ", "+student.getMosaicYear());
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

    }
}
