package app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class AppUtils {

    public static String randomImg() throws IOException  {
        String imagesPath = AppConfig.getInstance().getRawImgFolderPath();
        List<String> imgs = Files.walk(Paths.get(imagesPath))
                .map(Path::toFile)
                .filter(f ->f.getPath().endsWith(".tif"))
                .map(Objects::toString)
                .collect(Collectors.toList());

        return imgs.get(new Random().nextInt(imgs.size()));

    }
}
