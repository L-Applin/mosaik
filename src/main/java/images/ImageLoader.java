package images;

import javafx.scene.image.ImageView;

import java.io.IOException;

public interface ImageLoader {
    void loadImage(ImageView imageView, String pathToImg) throws IOException;
}
