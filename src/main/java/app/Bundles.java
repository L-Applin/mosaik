package app;

import java.util.ResourceBundle;

public enum Bundles {

    STRINGS("bundles/strings"),
    COLORS("bundles/colors"),
    CLI("bundles/cli"),
    PATH("bundles/path");

    String value;
    public String bundle() { return value; }

    Bundles(String value) {
        this.value=value;
    }

    public static ResourceBundle getResources(Bundles bundle){
        return ResourceBundle.getBundle(bundle.bundle(), AppConfig.getInstance().getLocale());
    }

}
