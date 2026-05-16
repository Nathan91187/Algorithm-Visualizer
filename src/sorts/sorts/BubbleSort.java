package sorts;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import main.Main;

import java.util.Arrays;
import java.util.Random;

public class BubbleSort {

    private static final int SIZE = 20;
    private static final int BAR_WIDTH = 30;

    private int[] array = new int[SIZE];
    private Rectangle[] bars = new Rectangle[SIZE];
    private Timeline timeline;

    @FXML private Pane visualPane;
    @FXML private Button startBtn;
    @FXML private Slider speedSlider;
    @FXML private Button backBtn;

    private Polygon indicator = new Polygon();
    private Main Main; // Reference to your main.java.org.example.Main class

    // Setter to pass main.java.org.example.Main instance
    public void setMain(Main main) {
        this.Main = main;
    }


    @FXML
    public void initialize() {
        // Generate data and draw bars
        generateArray();
        drawBars();
        setupIndicator();

        // Center bars on resize
        ChangeListener<Number> centerListener = (obs, oldVal, newVal) -> centerBars();
        visualPane.widthProperty().addListener(centerListener);
        visualPane.heightProperty().addListener(centerListener);
        visualPane.setStyle("-fx-background-color: linear-gradient(to bottom, #2b0000, #1a1a1a);");

        startBtn.setOnAction(e -> startBubbleSort(speedSlider.getValue()));
    }

    private void generateArray() {
        Random rand = new Random();
        for (int i = 0; i < SIZE; i++) {
            array[i] = rand.nextInt(200) + 20;
        }
    }

    private void drawBars() {
        visualPane.getChildren().clear();
        int maxHeight = Arrays.stream(array).max().orElse(0);

        for (int i = 0; i < SIZE; i++) {
            Rectangle bar = new Rectangle(0, 0, BAR_WIDTH - 2, array[i]);
            bar.setArcWidth(10);
            bar.setArcHeight(10);

            double ratio = array[i] / 220.0;
            bar.setFill(new LinearGradient(0, 0, 0, 1, true, null,
                    new Stop(0, Color.DODGERBLUE.interpolate(Color.LIGHTBLUE, ratio)),
                    new Stop(1, Color.LIGHTBLUE)));

            bars[i] = bar;
            visualPane.getChildren().add(bar);
        }

        visualPane.getChildren().add(indicator);
        centerBars();
    }

    private void centerBars() {
        if (bars == null) return;

        int maxHeight = Arrays.stream(array).max().orElse(0);
        double totalWidth = SIZE * (BAR_WIDTH + 4) - 4;
        double hOffset = (visualPane.getWidth() - totalWidth) / 2.0;
        double vOffset = (visualPane.getHeight() - maxHeight) / 2.0;

        for (int i = 0; i < SIZE; i++) {
            Rectangle bar = bars[i];
            bar.setX(hOffset + i * (BAR_WIDTH + 4));
            bar.setY(vOffset + maxHeight - array[i]);
        }

        // Keep indicator on correct bar
        if (indicator.isVisible()) {
            for (int i = 0; i < SIZE; i++) {
                if (Math.abs(indicator.getLayoutX() - (bars[i].getX() + BAR_WIDTH / 2.0)) < 0.1) {
                    moveIndicator(i);
                    break;
                }
            }
        }
    }

    private void setupIndicator() {
        indicator.getPoints().addAll(
                0.0, 0.0,
                10.0, 0.0,
                5.0, 10.0
        );
        indicator.setFill(Color.ORANGE);
        indicator.setVisible(false);
    }

    private void moveIndicator(int index) {
        if (index < 0 || index >= SIZE) {
            indicator.setVisible(false);
            return;
        }
        Rectangle bar = bars[index];
        indicator.layoutXProperty().bind(bar.xProperty().add(BAR_WIDTH / 3.0));
        indicator.layoutYProperty().bind(bar.yProperty().subtract(15));
        indicator.setVisible(true);
    }

    private void swap(int a, int b, Runnable onFinished) {
        Rectangle barA = bars[a];
        Rectangle barB = bars[b];

        double xA = barA.getX();
        double xB = barB.getX();

        double duration = 450 / speedSlider.getValue();

        Timeline swapAnim = new Timeline(
                new KeyFrame(Duration.millis(duration),
                        new KeyValue(barA.xProperty(), xB),
                        new KeyValue(barB.xProperty(), xA)
                )
        );

        swapAnim.setOnFinished(ev -> {
            int temp = array[a];
            array[a] = array[b];
            array[b] = temp;

            bars[a] = barB;
            bars[b] = barA;

            double pauseDuration = 150 / speedSlider.getValue();
            Timeline pause = new Timeline(new KeyFrame(Duration.millis(pauseDuration),
                    e -> onFinished.run()));
            pause.play();
        });

        swapAnim.play();
    }

    private void startBubbleSort(double speed) {
        if (timeline != null) timeline.stop();
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        final int[] i = {0};
        final int[] j = {0};
        final boolean[] comparing = {true};
        final boolean[] waiting = {false};

        KeyFrame frame = new KeyFrame(Duration.millis(50), e -> {
            if (waiting[0]) return;

            if (i[0] >= SIZE - 1) {
                moveIndicator(-1);
                for (Rectangle bar : bars) bar.setFill(Color.LIMEGREEN);
                timeline.stop();
                return;
            }

            if (j[0] < SIZE - i[0] - 1) {
                if (comparing[0]) {
                    moveIndicator(j[0]);
                    comparing[0] = false;

                    waiting[0] = true;
                    double trianglePause = 400 / speedSlider.getValue();
                    Timeline pause = new Timeline(new KeyFrame(Duration.millis(trianglePause),
                            ev -> waiting[0] = false));
                    pause.play();

                } else {
                    if (array[j[0]] > array[j[0] + 1]) {
                        waiting[0] = true;
                        swap(j[0], j[0] + 1, () -> waiting[0] = false);
                    }
                    comparing[0] = true;
                    j[0]++;
                }
            } else {
                int sortedIndex = SIZE - i[0] - 1;
                bars[sortedIndex].setFill(Color.LIMEGREEN);
                j[0] = 0;
                i[0]++;
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }
}
