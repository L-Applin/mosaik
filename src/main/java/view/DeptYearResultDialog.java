package view;

import app.AppConfig;
import app.Bundles;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import result.ResultItem;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class DeptYearResultDialog extends Dialog<String> {

    private DeptYearResultDialogController controller;

    public DeptYearResultDialog(List<ResultItem> results) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle(Bundles.STRINGS.bundle(), AppConfig.getInstance().getLocale());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/deptYearResultDialog.fxml"), bundle);
        Parent root = loader.load();
        getDialogPane().setContent(root);

        getDialogPane().getStylesheets().add("css/studentResultDialog.css");
        Scene scene = getDialogPane().getScene();
        Stage stage = ((Stage) scene.getWindow());
        scene.getStylesheets().add("css/studentResultDialog.css");

        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);

        controller = loader.getController();
        controller.addResults(results);

        ButtonType buttonTypeOk = new ButtonType(bundle.getString("confirm"), ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType(bundle.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        setResultConverter(btn -> {
            if (btn.getButtonData().isCancelButton()){
                close();
                return "";
            }
            ResultItem it = controller.resultList.getSelectionModel().getSelectedItem();
            return Objects.isNull(it)?"":it.getPath();
        });

    }

}
