package app;

import indexing.IMosaicInterface;
import indexing.IndexServiceFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.MainWindowController;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;


public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parameters parameters = getParameters();
        AppConfig.init(parameters);

        AppConfig config = AppConfig.getInstance();

        IMosaicInterface service = new IndexServiceFactory(config.getIndexLocation()).createService();
        service.setMosaicImagePath(config.getImgPathToFormat());

        if (config.requiesForceUpdate()){
            logger.info("mise à jour de l'index des mosaiques");
            service.update(config.getSourceFilePath());
            logger.info("mise à jour de l'index terminé");
        }


        if (config.requiresLaunch()) {
            URL mainFXML_URL = this.getClass().getClassLoader().getResource("fxml/main.fxml");
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/strings", config.getLocale());
            FXMLLoader loader = new FXMLLoader(mainFXML_URL, bundle);


            Parent root = loader.load();
            MainWindowController c = loader.getController();
            c.setMosaicInterface(service);
            c.initPostLoad();

            Scene scene = new Scene(root);
            primaryStage.setFullScreen(true);
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.show();
        }

    }


    public static void main(String[] args) {

        logger.info("Lancement de l'application");
        logger.info("Paramètres {}", Arrays.toString(args));

        launch(args);

    }


}
