package update;

import app.AppUtils;
import images.ImageLoader;
import images.TiffImageLoaderWithTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.Objects.nonNull;

@Slf4j
public class UpdateImgTask {

    private static class LONG {
        private static final long INITIAL_DELAY = 1000 * 60 * 3; // 3 minutes is : 60 sec * 1000 ms * 3
        private static final long PERIOD = 1000 * 20;            // 20seconds is : 20 sec * 1000 ms
    }

    private static class SHORT {
        private static final long INITIAL_DELAY = 1000 * 10; // 10 sec
        private static final long PERIOD = 1000 * 10;        // 20seconds is : 20 sec * 1000 ms
    }

    private static final long INITIAL_DELAY = SHORT.INITIAL_DELAY;
    private static final long PERIOD =        SHORT.PERIOD;

    private Timer timer;
    private ImageView imageView;
    private ImageLoader loader;

    private boolean isOpened;
    public boolean isOpened() { return isOpened; }
    public void setOpened(boolean opened) { isOpened = opened; }

    private Runnable onBegin;
    private Runnable onEnd;
    public UpdateImgTask setOnBegin (Runnable onBegin) { this.onBegin = onBegin; return this; }
    public UpdateImgTask setOnEnd   (Runnable onEnd)   { this.onEnd = onEnd;     return this; }

    public UpdateImgTask(ImageView imageView) {
        this.timer = new Timer();
        loader = new TiffImageLoaderWithTransition();
        this.imageView = imageView;
    }

    public void startUp(){
        log.info("timer starts");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!isOpened) {
                    Platform.runLater(updateImage);
                } else {
                    log.info("sideBar open : no change");
                }
            }
        };

        timer.schedule(task, INITIAL_DELAY, PERIOD);

    }

    private Runnable updateImage = () -> {
        executeRunanble(onBegin);
        try {
            loader.loadImage(imageView, AppUtils.randomImg());
        } catch (IOException e) {
            log.error("Impossible de trouver une image aléatoire", e);
        }
        executeRunanble(onEnd);
    };


    private void executeRunanble(Runnable runnable){
        if (nonNull(runnable)){
            runnable.run();
        }
    }

    public void cancel() {
        log.info("timer reset");
        timer.cancel();
        timer.purge();
        timer = new Timer();
    }
}
