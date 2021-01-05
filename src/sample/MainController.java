package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class MainController {
    public MainController() {

    }

    private Timeline timeline;
    private int index = 0;
    private String timerTime = "";
    private boolean cond;
    private MediaPlayer player;

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
        startButton.setDisable(true);

        startButton.setOnAction(a -> {
            setTimeDisable(true);
            startTimer();
            startButton.setVisible(false);
            pauseButton.setVisible(true);
            cancelButton.setVisible(true);
        });
        pauseButton.setOnAction(a -> {
            pauseTimer();
            resumeButton.setVisible(true);
            pauseButton.setVisible(false);
        });
        resumeButton.setOnAction(a -> {
            pauseTimer();
            resumeButton.setVisible(false);
            pauseButton.setVisible(true);
        });
        cancelButton.setOnAction(a -> {
            endTimer();
            setTimeDisable(false);
            hh.setText(timerTime.substring(0, 2));
            mm.setText(timerTime.substring(3, 5));
            ss.setText(timerTime.substring(6, 8));
            cond = true;
            initializeClear();
        });

        changeTime(hh);
        changeTime(mm);
        changeTime(ss);
    }

    private void initializeStopwatch() {
        initializeTimeline();
        startButton.setDisable(false);

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

    private void timeReset() {
        hh.setText("00");
        mm.setText("00");
        ss.setText("00");
    }

    //time changing
    private void changeTime(boolean check) {
        int s = Integer.parseInt(ss.getText());
        int m = Integer.parseInt(mm.getText());
        int h = Integer.parseInt(hh.getText());

        if (cond) {
            timerTime = hh.getText() + ":" + mm.getText() + ":" + ss.getText();
            cond = false;
        }

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


        } else {
            if (s != -1)
                s--;
            if (s == 0 && m == 0 && h == 0) {
                endTimer();
                timerEndSound();
                pauseButton.setVisible(false);
                cancelButton.setVisible(false);
                startButton.setVisible(true);
                startButton.setDisable(true);
                setTimeDisable(false);
            } else {
                if (s == -1) {
                    if (m == 0) {
                        h--;
                        s = 59;
                        m = 59;
                    } else {
                        m--;
                        s = 59;
                    }
                }
            }
        }

        ss.setText(((s / 10 == 0) ? "0" : "") + s);
        mm.setText(((m / 10 == 0) ? "0" : "") + m);
        hh.setText(((h / 10 == 0) ? "0" : "") + h);
    }

    private void changeTime(TextField tf) {
        tf.setOnMouseClicked(e -> {
            final String[] text = {tf.getText()};
            final boolean[] isTimeChanged = {false};
            tf.setText("");

            tf.setOnKeyTyped(event -> {
                Pattern pattern = Pattern.compile("[0-9]*");
                char c = event.getCharacter().charAt(0);
                Matcher matcher = pattern.matcher(Character.toString(c));

                if (matcher.matches()) {
                    tf.setText(text[0].charAt(1) + Character.toString(c));
                    text[0] = tf.getText();
                    isTimeChanged[0] = true;
                } else {
                    tf.setText("");
                    tf.setText(text[0]);
                }

                startButton.setDisable(isTimeEqualZero());
            });

            if (!isTimeChanged[0])
                tf.setText(text[0]);
        });
    }

   //utility
    private boolean isTimeEqualZero() {
        return (hh.getText().equals("00") &&
                mm.getText().equals("00") &&
                ss.getText().equals("00"));
    }

    private void textFlowClear() {
        textFlow.getChildren().clear();
        index = 0;
    }

    //timer handling
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

    private void startTimer() {
        timeline.play();
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

    //sound
    private void timerEndSound() {
        String path = "resources\\timer_end.mp3";
        Media media = new Media(new File(path).toURI().toString());
        player = new MediaPlayer(media);
        player.play();
    }

}
