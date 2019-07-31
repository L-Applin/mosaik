package app;

import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Wrapper around command line arguments passed to the jar file wen launching the Applincation from command line.
 * <p></p>
 * Accepted commands arguments are defined in the cli.properties file
 */
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public static final String KEY_PROP_PATH_INDEX = "index_path";

    public static final String KEY_PROP_PATH_TEST_IMG_PATH = "default_test_img_path";
    public static final String KEY_PROP_PATH_IMG_PATH = "default_img_path";
    public static final String KEY_PROP_PATH_DATA_PATH = "default_source_data_path";

    public static final String KEY_PROP_CLI_LANG = "softaware_langague";
    public static final String KEY_PROP_CLI_MOSAIC_IMG_PATH = "path_to_mosaic_image";
    public static final String KEY_PROP_CLI_FORCE_DATA_UPDATE = "force_update";
    public static final String KEY_PROP_CLI_LAUNCH_APP = "launch_app";


    private static AppConfig instance;
    public static AppConfig getInstance(){
        if (Objects.isNull(instance)){
           throw new RuntimeException("instance is not initialized");
        }
        return instance;
    }

    public static void init(Application.Parameters parameters){
        logger.info("initialisation des configurations");
        instance = new AppConfig(parameters);
    }


    /**
     * Index for search.
     */
    private String indexLocation;
    public Path getIndexLocation(){ return Paths.get(indexLocation); }


    /**
     * The Locale to be used for text formatting
     */
    private Locale locale;
    public Locale getLocale() { return locale; }


    /**
     * Forces an update on the data.
     */
    private boolean forceUpdate;
    public boolean requiesForceUpdate() { return forceUpdate; }


    /**
     * The path of the sourcefile to get the data update from. if not specified, will use the first .xls fil it
     * founds in the data folder at the same level of the jar file.
     *
     */
    private String sourceFilePath;
    public String getSourceFilePath() { return sourceFilePath; }


    /**
     * The path to the folder containing the mosaic images.
     */
    private String imgFolderPath;
    public String getImgPathToFormat() { return imgFolderPath; }

    private String rawImgFolderPath;
    public String getRawImgFolderPath() { return rawImgFolderPath; }

    /**
     * If the appshould launch or not. Can be set to false to make an update on the datawithout launching the
     * application
     */
    private boolean launch;
    public boolean requiresLaunch() { return launch; }


    public AppConfig(Application.Parameters parameters) {

        // TODO: 2019-04-20 for all pahts = path validation (well formed, and exists)
        ResourceBundle paths = ResourceBundle.getBundle(Bundles.PATH.bundle(), Locale.ROOT);
        // default values
        this.indexLocation = paths.getString(KEY_PROP_PATH_INDEX);
        this.launch=true;
        this.locale=Locale.ROOT;
        this.imgFolderPath=paths.getString(KEY_PROP_PATH_TEST_IMG_PATH); //todo : replace by default before launch
        this.forceUpdate=false;
        this.sourceFilePath=paths.getString(KEY_PROP_PATH_DATA_PATH);

        Map<String, String> args = parameters.getNamed();
        ResourceBundle cliArgsKey = ResourceBundle.getBundle(Bundles.CLI.bundle(), Locale.ROOT);


        // launch app
        String launchKey = cliArgsKey.getString(KEY_PROP_CLI_LAUNCH_APP);
        if (args.containsKey(launchKey)){
            String launchValue = args.get(launchKey);
            if (launchValue.toLowerCase().equals("f") || launchValue.toLowerCase().equals("false")){
                launch=false;
            }
        }


        // locale
        String langKey = cliArgsKey.getString(KEY_PROP_CLI_LANG);
        if (args.containsKey(langKey)){
            String localeStr = args.get(langKey);
            if (localeStr!=null && localeStr.equals("en")){
                locale = Locale.ENGLISH;
                logger.info("Application version anglaise");
            }
        }


        // mosaic image path
        String mosImgKey = cliArgsKey.getString(KEY_PROP_CLI_MOSAIC_IMG_PATH);
        if (args.containsKey(mosImgKey)){
            rawImgFolderPath = args.get(mosImgKey);
            if (rawImgFolderPath!=null) {
                imgFolderPath=rawImgFolderPath+"/%s/%s_%s.tif";
            }
        }



        // updates
        try {
            String updateKey = cliArgsKey.getString(KEY_PROP_CLI_FORCE_DATA_UPDATE);
            if (args.containsKey(updateKey)) {
                forceUpdate = true;
                String updateDataPath = args.get(updateKey);
                if (updateDataPath == null || updateDataPath.equals("")) {
                    Path path = Files.list(Paths.get("./data"))
                            .filter(Files::isRegularFile)
                            .filter(path1 -> path1.toString().endsWith(".csv") || path1.toString().endsWith(".xls"))
                            .findAny()
                            .orElseThrow(() -> new FileNotFoundException("No update file found at default location " + Paths.get("./data").toString()));

                    logger.info("configuration de chemin d'accèes {}", path);
                    sourceFilePath = path.toString();

                } else {
                    if (!(updateDataPath.endsWith(".csv") || updateDataPath.endsWith(".xls"))) {
                        throw new FileNotFoundException("No update file found at location " + updateDataPath);
                    }
                    sourceFilePath = updateDataPath;
                }
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
            logger.error("Error while trying to execute update : " + ioe.getMessage(), ioe);
            forceUpdate=false;
        }
    }

}
