package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import java.awt.event.KeyEvent;

public class MainController {
    public MainController() {

    }

    Timeline timeline;
    int index = 0;

    @FXML
    private TextFlow textFlow = new TextFlow();

    @FXML
    private RadioButton timerButton;

    @FXML
    private RadioButton stopWatchButton;

    @FXML
    private TextField hh;

    @FXML
    private TextField mm;

    @FXML
    private TextField ss;

    @FXML
    private Button stopButton;

    @FXML
    private Button lapButton;

    @FXML
    private Button startButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button resumeButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button cancelButton;

    //init
    @FXML
    private void initialize() {
        setTimeDisable(true);
        initializeStopwatch();

        timerButton.setOnAction(a -> {
            endTimer();
            setTimeDisable(false);
            initializeClear();
            timeReset();
            initializeTimer();
        });

        stopWatchButton.setOnAction(a -> {
            setTimeDisable(true);
            initializeClear();
            timeReset();
            initializeStopwatch();
        });
    }

    private void initializeClear() {
        pauseButton.setVisible(false);
        stopButton.setVisible(false);
        cancelButton.setVisible(false);
        resumeButton.setVisible(false);
        lapButton.setVisible(false);
        resetButton.setVisible(false);
        startButton.setVisible(true);
    }

    private void initializeTimer() {
        textFlowClear();

        startButton.setOnAction(a -> {
            setTimeDisable(true);
            startButton.setVisible(false);
            pauseButton.setVisible(true);
            cancelButton.setVisible(true);
        });

        pauseButton.setOnAction(a -> {
            resumeButton.setVisible(true);
            pauseButton.setVisible(false);
        });

        resumeButton.setOnAction(a -> {
            resumeButton.setVisible(false);
            pauseButton.setVisible(true);
        });

        cancelButton.setOnAction(a -> {
            setTimeDisable(false);
            initializeClear();
        });
    }

    private void initializeStopwatch() {
        initializeTimeline();

        startButton.setOnAction(a -> {
            startTimer();
            startButton.setVisible(false);
            stopButton.setVisible(true);
            lapButton.setVisible(true);
        });

        stopButton.setOnAction(a -> {
            pauseTimer();
            stopButton.setVisible(false);
            lapButton.setVisible(false);
            resumeButton.setVisible(true);
            resetButton.setVisible(true);
        });

        resetButton.setOnAction(a -> {
            textFlowClear();
            endTimer();
            resumeButton.setVisible(false);
            resetButton.setVisible(false);
            startButton.setVisible(true);
        });

        resumeButton.setOnAction(a -> {
            pauseTimer();
            resumeButton.setVisible(false);
            resetButton.setVisible(false);
            stopButton.setVisible(true);
            lapButton.setVisible(true);
        });

        lapButton.setOnAction(a -> timerLap());
    }

    private void initializeTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(1000),
                actionEvent -> changeTime(stopWatchButton.isSelected())));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(false);
    }

    //time control

    private void setTimeDisable(boolean value) {
        hh.setEditable(!value);
        hh.setMouseTransparent(value);
        hh.setFocusTraversable(!value);
        mm.setEditable(!value);
        mm.setMouseTransparent(value);
        mm.setFocusTraversable(!value);
        ss.setEditable(!value);
        ss.setMouseTransparent(value);
        ss.setFocusTraversable(!value);
    }

    private void startTimer() {
        timeline.play();
    }

    private void timeReset() {
        hh.setText("00");
        mm.setText("00");
        ss.setText("00");
    }

    private void pauseTimer() {
        if (timeline.getStatus().equals(Animation.Status.PAUSED))
            timeline.play();
        else if (timeline.getStatus().equals(Animation.Status.RUNNING)) {
            timeline.pause();
        }

    }

    private void endTimer() {
        timeline.stop();
        timeReset();
    }

    // time changing
    private void changeTime(boolean check) {
        int s = Integer.parseInt(ss.getText());
        int m = Integer.parseInt(mm.getText());
        int h = Integer.parseInt(hh.getText());

        if (check) {

            s++;
            if (s == 60) {
                m++;
                s = 0;
            }

            if (m == 60) {
                h++;
                m = 0;
            }

            ss.setText(((s / 10 == 0) ? "0" : "") + s);
            mm.setText(((m / 10 == 0) ? "0" : "") + m);
            hh.setText(((h / 10 == 0) ? "0" : "") + h);
        } else {
            s--;
            // TODO: SUPER HARDWORKING TIMER MECHANISM
        }
    }

    private void textFlowClear() {
        textFlow.getChildren().clear();
        index = 0;
    }

    private void timerLap() {
        TextField text = new TextField();
        TextField tmp = new TextField();

        if (index >= 5) {
            textFlow.getChildren().remove(0);
        }
        index++;
        tmp.setText(index + ": ");

        text.setText(tmp.getText() + hh.getText() + ":" + mm.getText() + ":" + ss.getText() + "\n");
        text.setAlignment(Pos.CENTER);

        textFlow.getChildren().add(text);

    }

    //timer handling

    private void gettingChanged(){
        //TODO: change time text on click

    }

    private void textChange(KeyEvent event,TextField tf){
        char c = event.getKeyChar();

        if (Character.isLetter(c))
            tf.setEditable(false);
         else
            tf.setEditable(true);
    }


}
