package images;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;

public class TiffImageLoaderWithTransition extends TifImageLoader {

    @Override
    public void loadImage(ImageView imageView, String pathToImg) throws IOException {
        Image nextImage = getImage(pathToImg);
        SequentialTransition fade = createTransition(imageView, nextImage);
        fade.play();
    }

    SequentialTransition createTransition(final ImageView iv, final Image img){
        FadeTransition fadeOutTransition = new FadeTransition(Duration.seconds(1), iv);
        fadeOutTransition.setFromValue(1.0);
        fadeOutTransition.setToValue(0.0);
        fadeOutTransition.setOnFinished(event -> iv.setImage(img));
        FadeTransition fadeInTransition = new FadeTransition(Duration.seconds(1), iv);
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(1.0);
        SequentialTransition seq = new SequentialTransition();
        seq.getChildren().addAll(fadeOutTransition, fadeInTransition);
        return seq;
    }
}
