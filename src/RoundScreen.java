import processing.core.PApplet;
import processing.core.PVector;

public class RoundScreen {
    PApplet app;
    int roundNumber;
    int countDownTimer;
    int score;
    int cityScore;
    long startTime;
    boolean roundStarted;
    int roundTextSize;
    int scoreRoundTextSize;
    PVector startRoundPosition;
    int score_X_Position;
    int round_X_Position;
    int score_round_Y_position;

    public RoundScreen(PApplet app, int roundNumber, int countDownTimer, int score,
                       int roundTextSize, int scoreRoundTextSize, PVector startRoundPosition,
                       int scorePosition, int roundPosition, int score_round_Y_position) {
        this.app = app;
        this.roundNumber = roundNumber;
        this.countDownTimer = countDownTimer;
        this.score = score;
        this.startTime = System.currentTimeMillis();
        this.roundTextSize = roundTextSize;
        this.scoreRoundTextSize = scoreRoundTextSize;
        this.startRoundPosition = startRoundPosition;
        this.round_X_Position = roundPosition;
        this.score_X_Position = scorePosition;
        this.score_round_Y_position = score_round_Y_position;
        roundStarted = false;
    }

    void draw() {
        int time = (int) ((System.currentTimeMillis() - startTime)/1000);
        roundStarted = countDownTimer - time < 0;
        if(!roundStarted) {
            app.textSize(roundTextSize);
//            app.text("Wave " + roundNumber + " in T-" + (countDownTimer-time),
//                    startRoundPosition.x, startRoundPosition.y);
            app.text("Wave " + roundNumber,
                    startRoundPosition.x, startRoundPosition.y);
        }
        app.textSize(scoreRoundTextSize);
        app.text("score: " + score, score_X_Position, score_round_Y_position);
        app.text("round: " + roundNumber, round_X_Position, score_round_Y_position);
    }

    void newRound() {
        roundNumber++;
        startTime = System.currentTimeMillis();
        roundStarted = false;
    }

    void destroyedAsteroid() {
        score += 100;
        cityScore += 100;
    }

    boolean hasRoundStarted() {
        return roundStarted;
    }
}
