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

public class SelectionSort {

    private static final int SIZE = 20;
    private static final int BAR_WIDTH = 30;

    private int[] array = new int[SIZE];
    private Rectangle[] bars = new Rectangle[SIZE];
    private Timeline timeline;

    @FXML private Pane visualPane;
    @FXML private Button startBtn;
    @FXML private Slider speedSlider;
    @FXML private Button backBtn;

    private Polygon orangeTriangle = new Polygon();
    private Polygon redTriangle = new Polygon();
    private Main Main;

    public void setMain(Main main) {
        this.Main = main;
    }



    @FXML
    public void initialize() {
        generateArray();
        drawBars();
        setupTriangles();

        ChangeListener<Number> centerListener = (obs, oldVal, newVal) -> centerBars();
        visualPane.widthProperty().addListener(centerListener);
        visualPane.heightProperty().addListener(centerListener);
        visualPane.setStyle("-fx-background-color: linear-gradient(to bottom, #2b0000, #1a1a1a);");

        startBtn.setOnAction(e -> startSelectionSort());
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

        // Add triangles on top of bars
        if (!visualPane.getChildren().contains(orangeTriangle))
            visualPane.getChildren().add(orangeTriangle);
        if (!visualPane.getChildren().contains(redTriangle))
            visualPane.getChildren().add(redTriangle);

        orangeTriangle.setVisible(false);
        redTriangle.setVisible(false);

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

        updateTrianglePositions();
    }

    private void setupTriangles() {
        orangeTriangle.getPoints().setAll(0.0,0.0,10.0,0.0,5.0,10.0);
        orangeTriangle.setFill(Color.ORANGE);
        orangeTriangle.setVisible(false);

        redTriangle.getPoints().setAll(0.0,0.0,10.0,0.0,5.0,10.0);
        redTriangle.setFill(Color.RED);
        redTriangle.setVisible(false);
    }

    private void updateTrianglePositions() {
        if (orangeTriangle.isVisible() && orangeTriangle.getUserData() != null) {
            int idx = (int) orangeTriangle.getUserData();
            if (idx >= 0 && idx < SIZE) {
                Rectangle bar = bars[idx];
                orangeTriangle.setLayoutX(bar.getX() + BAR_WIDTH / 3.0);
                orangeTriangle.setLayoutY(bar.getY() - 30);
            }
        }
        if (redTriangle.isVisible() && redTriangle.getUserData() != null) {
            int idx = (int) redTriangle.getUserData();
            if (idx >= 0 && idx < SIZE) {
                Rectangle bar = bars[idx];
                redTriangle.setLayoutX(bar.getX() + BAR_WIDTH / 3.0);
                redTriangle.setLayoutY(bar.getY() - 15);
            }
        }
    }

    private void swap(int a, int b, Runnable onFinished) {
        Rectangle barA = bars[a];
        Rectangle barB = bars[b];

        double xA = barA.getX();
        double xB = barB.getX();

        double duration = 700 / speedSlider.getValue();

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

            Timeline pause = new Timeline(new KeyFrame(Duration.millis(500 / speedSlider.getValue()),
                    e -> onFinished.run()));
            pause.play();
        });

        swapAnim.play();
    }

    private void startSelectionSort() {
        if (timeline != null) timeline.stop();
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        final int[] i = {0};           // current sorted index
        final int[] j = {i[0] + 1};    // scanning index
        final int[] minIndex = {i[0]};
        final boolean[] waiting = {false};

        // Show triangles
        orangeTriangle.setVisible(true);
        redTriangle.setVisible(true);
        orangeTriangle.setUserData(i[0]);
        redTriangle.setUserData(j[0]);
        updateTrianglePositions();

        KeyFrame frame = new KeyFrame(Duration.millis(800/speedSlider.getValue()), e -> {
            if (waiting[0]) return;

            if (i[0] >= SIZE - 1) {
                // done
                orangeTriangle.setVisible(false);
                redTriangle.setVisible(false);
                bars[SIZE-1].setFill(Color.LIMEGREEN);
                timeline.stop();
                return;
            }

            if (j[0] < SIZE) {
                // move red triangle
                redTriangle.setUserData(j[0]);
                updateTrianglePositions();

                // check for new minimum
                if (array[j[0]] < array[minIndex[0]]) {
                    minIndex[0] = j[0];
                    orangeTriangle.setUserData(minIndex[0]);
                    updateTrianglePositions();
                }

                j[0]++;
            } else {
                // swap if needed
                if (minIndex[0] != i[0]) {
                    waiting[0] = true;
                    swap(i[0], minIndex[0], () -> {
                        bars[i[0]].setFill(Color.LIMEGREEN); // mark sorted
                        i[0]++;
                        j[0] = i[0] + 1;
                        minIndex[0] = i[0];
                        orangeTriangle.setUserData(i[0]);
                        redTriangle.setUserData(j[0]);
                        updateTrianglePositions();
                        waiting[0] = false;
                    });
                } else {
                    // no swap, just mark sorted
                    bars[i[0]].setFill(Color.LIMEGREEN);
                    i[0]++;
                    j[0] = i[0] + 1;
                    minIndex[0] = i[0];
                    orangeTriangle.setUserData(i[0]);
                    redTriangle.setUserData(j[0]);
                    updateTrianglePositions();
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }
}
