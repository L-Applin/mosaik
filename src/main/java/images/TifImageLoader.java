package images;

import ij.ImagePlus;
import ij.io.Opener;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class TifImageLoader implements ImageLoader {

    @Override
    public void loadImage(ImageView imageView, String pathToImg) throws IOException {
        imageView.setImage(getImage(pathToImg));
    }

    protected Image getImage(String pathToImg) throws IOException {
        ImagePlus imagePlus = new Opener().openTiff(pathToImg, "");

        if (Objects.isNull(imagePlus)){
            throw new IOException("Cannot locate file: "+pathToImg);
        }

        BufferedImage bufferedImage = imagePlus.getBufferedImage();
        return SwingFXUtils.toFXImage(bufferedImage, null);

    }

}
