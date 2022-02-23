import processing.core.PApplet;
import processing.core.PVector;

/**
 * stores all the information with regards to waves, scores, and timers for rounds
 */
public class RoundScreen {
    PApplet app;
    int roundNumber;
    int countDownTimer;
    int score; // real score shown on screen
    int cityScore; // score to determine if city can be rebuilt
    int multiplier; // score multiplier
    long startTime;
    boolean roundStarted;
    int roundTextSize;
    int scoreRoundTextSize;
    PVector startRoundPosition;
    int score_X_Position;
    int round_X_Position;
    int score_round_Y_position;

    /**
     * constructor
     * @param app app
     * @param roundNumber starting round number
     * @param countDownTimer how long to countdown between waves
     * @param score current score
     * @param roundTextSize size of the round text
     * @param scoreRoundTextSize size of the score and round text at the top of the screen
     * @param startRoundPosition position of the start round text
     * @param scorePosition where the score is positioned in x
     * @param roundPosition where the round is positioned in x
     * @param score_round_Y_position where the round score y position is
     */
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
        this.multiplier = 1;
        roundStarted = false;
    }

    /**
     * draws the round screen
     */
    void draw() {
        if(!roundStarted) { // displaying current round number
            app.textSize(roundTextSize);
//            app.text("Wave " + roundNumber + " in T-" + (countDownTimer-time),
//                    startRoundPosition.x, startRoundPosition.y);
            app.text("Wave " + roundNumber,
                    startRoundPosition.x, startRoundPosition.y);
        }
        app.textSize(scoreRoundTextSize);
        app.text("score: " + score + "  x" + multiplier, score_X_Position, score_round_Y_position);
        app.text("round: " + roundNumber, round_X_Position, score_round_Y_position);
    }

    /**
     * determines when the round is going to start
     */
    void computeRoundStart() {
        int time = (int) ((System.currentTimeMillis() - startTime)/1000);
        roundStarted = countDownTimer - time < 0;
    }

    /**
     * initialises a new round
     */
    void newRound() {
        roundNumber++;
        if(roundNumber < 12) multiplier = (int) Math.ceil(roundNumber/2.0); // adjust the score multiplier as need be
        startTime = System.currentTimeMillis();
        roundStarted = false;
    }

    /**
     * add the points with multiplier
     * @param points points to add
     */
    void addScore(int points) {
        score += points*multiplier;
        cityScore += points*multiplier;
    }

    /**
     * if round has started
     * @return true if round has started
     */
    boolean hasRoundStarted() {
        return roundStarted;
    }
}
