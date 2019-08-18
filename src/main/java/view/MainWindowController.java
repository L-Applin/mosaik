package view;

import app.AppConfig;
import app.AppUtils;
import app.Bundles;
import ij.ImagePlus;
import ij.io.Opener;
import images.ImageLoader;
import images.TifImageLoader;
import indexing.IMosaicInterface;
import indexing.IndexService;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import result.MosaicSearchResult;
import update.UpdateImgTask;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;


@FXMLController("fxml/main.fxml")
public class MainWindowController {

    private static final Logger logger = LoggerFactory.getLogger(MainWindowController.class);

    private static List<String> defaut_dept_names = Arrays.asList("Physique", "Chimie", "Géographie", "Sciences biologiques");
    private static List<String> defaut_years;
    static {
        ArrayList<String> years = new ArrayList<>();
        for (int i = 1951; i<2018;i++){
            years.add(i+"");
        }
        defaut_years=years;
    }

    @FXML private Pane               sidebarMenu;
    @FXML private VBox               sidebarMenuContent;
    @FXML private ComboBox<String>   departementsCombo;
    @FXML private ComboBox<String>   gradYearCombo;
    @FXML private StackPane          mainView;
    @FXML private ImageView          udemLogo;
    @FXML private ImageView          mosaicImage;
    // @FXML private Button             configBtn;
    @FXML private TextField          studentNameField;


    private EventType<Event> sideBarControl;
    private SidebarEventHandler sidebarEventHandler;
    private GaussianBlur blurr;

    private UpdateImgTask updateImgTask;

    /**
     * The search interface
     */
    private IMosaicInterface service;
    public void setMosaicInterface(IMosaicInterface service) { this.service = service; }



    @FXML
    void initialize(){

        ReadOnlyDoubleProperty height = mainView.heightProperty();
        ReadOnlyDoubleProperty width = mainView.widthProperty();

        // make sideBar full height
        sidebarMenu.prefHeightProperty().bind(height);
        sidebarMenuContent.prefHeightProperty().bind(height);

        //make image view full height-width
        mosaicImage.fitWidthProperty().bind(width);
        mosaicImage.fitHeightProperty().bind(height);


        udemLogo.prefWidth(sidebarMenu.widthProperty().doubleValue());
        departementsCombo.prefWidth(sidebarMenuContent.getWidth());

        // animation sidebar
        sideBarControl = new EventType<>("side_bar_control");
        sidebarEventHandler = new SidebarEventHandler(350);
        mainView.addEventHandler(sideBarControl, sidebarEventHandler);
        mainView.setOnTouchReleased(event -> {});

        try {

            String img = AppUtils.randomImg();

            logger.info("Mosaique aléatoire choisie : {}", img);
            ImagePlus imagePlus = new Opener().openTiff(img, "");
            if (imagePlus != null) {
                BufferedImage bufferedImage = imagePlus.getBufferedImage();
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                mosaicImage.setImage(image);

                //gaussian Blurr
                blurr = new GaussianBlur(15);
                mosaicImage.setEffect(blurr);
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        updateImgTask = new UpdateImgTask(mosaicImage)
                .setOnBegin(() -> logger.info("loading random image"))
                .setOnEnd(() -> logger.info("random image loaded"));

        updateImgTask.setOpened(true);
        updateImgTask.startUp();

    }



    public void initPostLoad(){

        try{
            Set<String> depts = service.getAllValueForFieldName(IndexService.dept_field);
            depts.remove("");
            departementsCombo.getItems().addAll(depts);
            departementsCombo.getItems().add(0, "- Aucun -");
        } catch (IOException ioe) {
            logger.error(ioe.getLocalizedMessage(), ioe);
            logger.warn("Chargement des départements avec valeurs par défaut !");
            departementsCombo.getItems().addAll(defaut_dept_names);
        }

        try{
            //todo change for year_field
            Set<String> years = service.getAllValueForFieldName(IndexService.year_field);
            gradYearCombo.getItems().addAll(years);
            gradYearCombo.getItems().add(0, "- Aucune -");
        } catch (IOException ioe) {
            logger.error(ioe.getLocalizedMessage(), ioe);
            logger.warn("Chargement des années avec valeurs par défaut !");
            departementsCombo.getItems().addAll(defaut_years);
        }

    }



    @FXML
    public void launchSearch(ActionEvent actionEvent) {

        String dept = "- Aucun -".equals(departementsCombo.getValue())?null:departementsCombo.getValue();
        String year = "- Aucune -".equals(gradYearCombo.getValue())?null:gradYearCombo.getValue();
        String name = studentNameField.getText();
        name = Objects.isNull(name)?name:name.toLowerCase();

        logger.info("\nsearching for mosaique : %s %s, %s\n", dept, year, name);

        try {
            MosaicSearchResult res = service.search(name, dept, year);
            if (name!=null && !name.trim().equals("")){
                handleStudentResults(res);
            } else {
                handleDeptYearResult(res);
            }

        } catch (IOException ioe){
            // TODO: 2019-03-23 UI feedback
            logger.error("une erreur s'est produite durant la recherche dept={}, year={} name={}", dept, year, name);
            logger.error(ioe.getMessage(), ioe);
        }

    }



    private void handleStudentResults(MosaicSearchResult res) {
        logger.info("résultat de la recherche pour un étudiant a rapporté {} résultats", res.getResults().size());
        try {
            StudentResultDialog dialog = new StudentResultDialog(res.getResults());
            dialog.showAndWait().ifPresent(this::updateMosaicImg);
            // close sidebar
        } catch (IOException ioe){
            dialogError(ioe);
        }
    }


    private void handleDeptYearResult(MosaicSearchResult res){
        logger.info("résultat de la recherche pour un dept/année a rapporté {} résultats", res.getResults().size());
        try {
            DeptYearResultDialog dialog = new DeptYearResultDialog(res.getResults());
            dialog.showAndWait().ifPresent(this::updateMosaicImg);
        } catch (IOException ioe) {
            dialogError(ioe);
        }
    }


    private void updateMosaicImg(String path) {

        if (path.equals("")){
            logger.info("Aucune sélection");
            return;
        }

        ImageLoader loader = new TifImageLoader();

        try {
            loader.loadImage(mosaicImage, path);
            mainView.fireEvent(new Event(sideBarControl));
        } catch (IOException ioe){
            logger.error("erreur pendant l'affichage de l'image {}", path);
            logger.error(ioe.getMessage(), ioe);
        }
    }


    private void dialogError(IOException ioe){
        ioe.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        ResourceBundle bundle = ResourceBundle.getBundle(Bundles.STRINGS.bundle(), AppConfig.getInstance().getLocale());
        alert.setContentText(bundle.getString("dialog_error"));
        alert.show();
    }


    public void sidebarControl(TouchEvent touchEvent) {
        System.out.println("touch main");
        mainView.fireEvent(new Event(sideBarControl));

    }


    public void sidebarControlMouse(MouseEvent mouseEvent) {
        updateImgTask.cancel();
        sidebarEventHandler.setMaxWidth(sidebarMenu.getBoundsInParent().getWidth());
        mainView.fireEvent(new Event(sideBarControl));
        updateImgTask.startUp();

    }




    /**
     * Sidebar event handler for animating the opening and closing
     */
    private class SidebarEventHandler implements EventHandler<Event> {
        private double maxWidth;
        private double animationDuration;
        public SidebarEventHandler(double animationDuration) {
            this.animationDuration = animationDuration;
        }

        public void setMaxWidth(double maxWidth) { this.maxWidth = maxWidth; }

        @Override
        public void handle(Event actionEvent) {

            // create an animation to hide sidebar.
            final Animation hideSidebar = new Transition() {
                { setCycleDuration(Duration.millis(animationDuration)); }
                @Override protected void interpolate(double frac) {
                    final double curWidth = maxWidth * (1.0 - frac);
                    //sidebarMenu.setPrefWidth(curWidth);
                    sidebarMenu.setTranslateX(-maxWidth + curWidth);
                }
            };

            hideSidebar.onFinishedProperty().set(__ -> {
                sidebarMenu.setVisible(false);
                mosaicImage.effectProperty().setValue(null);
                updateImgTask.setOpened(false);
            });

            // create an animation to show a sidebar.
            final Animation showSidebar = new Transition() {
                { setCycleDuration(Duration.millis(animationDuration)); }
                @Override protected void interpolate(double frac) {
                    final double curWidth = maxWidth * frac;
                    sidebarMenu.setTranslateX(-maxWidth + curWidth);
                    departementsCombo.getSelectionModel().clearSelection();
                    gradYearCombo.getSelectionModel().clearSelection();
                }
            };

            showSidebar.onFinishedProperty().set(__ -> {
                sidebarMenu.setVisible(true);
                mosaicImage.setEffect(blurr);
                updateImgTask.setOpened(true);
            });



            if (showSidebar.statusProperty().get() == Animation.Status.STOPPED &&
                    hideSidebar.statusProperty().get() == Animation.Status.STOPPED) {
                if (sidebarMenu.isVisible()) {
                    hideSidebar.play();
                } else {
                    sidebarMenu.setVisible(true);
                    showSidebar.play();
                }
            }
        }
    }
}

