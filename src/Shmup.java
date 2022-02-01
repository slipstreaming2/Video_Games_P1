import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;

// LAAAAAAG IMPLEMEEEENT

public class Shmup extends PApplet {
// how will you handle different phases: pre-game, game, post-game?
    public static void main(String[] args) {
        PApplet.main("Shmup");
    }

    final int MISSILE_WIDTH_PROPORTION = 50,
            MISSILE_HEIGHT_PROPORTION = 50,
            MISSILE_INCREMENT_PROPORTION = 10;

    final int NUM_MISSILE_PER_BALLISTA = 10;

    final int CROSSHAIR_WIDTH_PROPORTION = 100,
            CROSSHAIR_HEIGHT_PROPORTION = 100,
            CROSSHAIR_OFFSET_PROPORTION = 50;

    final double BALLISTA_HEIGHT_PROPORTION = 30,
            BALLISTA_WIDTH_PROPORTION = 55.0/5.0,
            BALLISTA_LEFT_INIT_X_PROPORTION = 110.0/11.0,
            BALLISTA_MIDDLE_INIT_X_PROPORTION = 2,
            BALLISTA_RIGHT_INIT_X_PROPORTION = 110.0/99.0;

    final double CITY_HEIGHT_PROPORTION = 25,
            CITY_WIDTH_PROPORTION = 55.0/3.0,
            CITY_LEFT_1_INTI_X_PROPORTION = 110.0/23.0,
            CITY_LEFT_2_INIT_X_PROPORTION = 110.0/33.0,
            CITY_LEFT_3_INIT_X_PROPORTION = 110.0/43.0,
            CITY_RIGHT_1_INIT_X_PROPORTION = 110.0/67.0,
            CITY_RIGHT_2_INIT_X_PROPORTION = 110.0/77.0,
            CITY_RIGHT_3_INIT_X_PROPORTION = 110.0/87.0;

    final int EXPLOSION_RATIO_PROPORTION = 1000,
            EXPLOSION_RADIUS_PROPORTION = 50;

    final int TEXT_SIZE_PROPORTION = 50,
            BALLISTA_TEXT_OFFSET_PROPORTION = 1000;

    final int STARTING_ASTEROID_COUNT = 10,
            ASTEROID_WIDTH_PROPORTION = 50,
            STARTING_ASTEROID_VELOCITY = 5,
            STARTING_MAX_NUM_ASTEROIDS_ON_SCREEN = 3,
            INCREMENT_MAX_NUM_ASTEROIDS = 1;

    final int ROUND_START_TEXT_PROPORTION = 30,
            ROUND_START_TEXT_X_PROPORTION = 3,
            ROUND_START_TEXT_Y_PROPORTION = 2,
            SCORE_ROUND_TEXT_PROPORTION = 50,
            SCORE_X_OFFSET_PROPORTION = 10,
            ROUND_X_OFFSET_PROPORTION = 2,
            SCORE_ROUND_Y_OFFSET_PROPORTION = 10;

    final int STARTING_PROBABILITY_OF_SPAWN = 10,
            INCREMENT_PER_ROUND = 5,
            MAX_DELAY_BETWEEN_SPAWNS = 2;

    ArrayList<Ballista> ballistaSelector;
    Queue<PlayerMissile> missilesToDisplay;
    Queue<Explosion> explosionsToDisplay;
    Queue<Explosion> explosionsToUse;
    Crosshair crosshair;
    ArrayList<City> cities;
    Queue<Asteroid> asteroidsToDisplay;
    Queue<Asteroid> asteroidsToUse;
    RoundScreen roundScreen;

    int numAsteroidsThisRound = STARTING_ASTEROID_COUNT;
    int maxScreenAsteroids = STARTING_MAX_NUM_ASTEROIDS_ON_SCREEN;
    int incrementAsteroids = INCREMENT_MAX_NUM_ASTEROIDS;
    int probability = STARTING_PROBABILITY_OF_SPAWN;
    int selectedBallista = 0;
    int last_asteroid_spawn;
    int last_second_checked;

    Random rand;





    // Initialise display and game elements
// NB Accessing displayWidth/Height pre setup() doesn't seem to work out
    // settings -> setup
    public void setup() {
        rectMode(CENTER);
        rand = new Random();
        last_asteroid_spawn = (int) (System.currentTimeMillis()/1000);
        last_second_checked = last_asteroid_spawn;

        /*
        BALLISTA + EXPLOSIONS SETUP
         */
        double ballistaWidth = displayWidth / BALLISTA_WIDTH_PROPORTION,
            ballistaHeight = displayHeight / BALLISTA_HEIGHT_PROPORTION,
            ballistaInitY = displayHeight - ballistaHeight;

        double[] ballistaInitXs = new double[]{BALLISTA_LEFT_INIT_X_PROPORTION,
                BALLISTA_MIDDLE_INIT_X_PROPORTION,
                BALLISTA_RIGHT_INIT_X_PROPORTION};

        ballistaSelector = new ArrayList<>();
        explosionsToUse = new LinkedList<>();


        for(int i = 0 ; i < ballistaInitXs.length ; i++) {
            Stack<PlayerMissile> missiles = new Stack<>();
            for (int j = 0; j < NUM_MISSILE_PER_BALLISTA; j++) {
                missiles.push(new PlayerMissile(0, 0,
                        displayWidth / MISSILE_WIDTH_PROPORTION,
                        displayHeight / MISSILE_HEIGHT_PROPORTION,
                        MISSILE_INCREMENT_PROPORTION, this));
            }
            Ballista ballista = new Ballista(this, (int) (displayWidth / ballistaInitXs[i]),
                    (int) ballistaInitY, (int) ballistaHeight, (int) ballistaWidth, missiles,
                                                displayHeight/TEXT_SIZE_PROPORTION,
                                                displayHeight/BALLISTA_TEXT_OFFSET_PROPORTION);
            ballistaSelector.add(ballista);
            explosionsToUse.offer(new Explosion(this, 0, 0, 0,
                    displayWidth/EXPLOSION_RATIO_PROPORTION,
                    displayWidth/EXPLOSION_RADIUS_PROPORTION));
        }

        /*
        CITIES SETUP
         */
        double[] citiesInitXs = new double[] {CITY_LEFT_1_INTI_X_PROPORTION,
                CITY_LEFT_2_INIT_X_PROPORTION, CITY_LEFT_3_INIT_X_PROPORTION,
                CITY_RIGHT_1_INIT_X_PROPORTION, CITY_RIGHT_2_INIT_X_PROPORTION,
                CITY_RIGHT_3_INIT_X_PROPORTION};
        cities = new ArrayList<>();
        double cityHeight = displayHeight/CITY_HEIGHT_PROPORTION,
                cityWidth = displayWidth/CITY_WIDTH_PROPORTION,
                cityInitY = displayHeight - cityHeight;

        for(int i = 0 ; i < citiesInitXs.length ; i++) {
            City city = new City(this, (int)cityHeight, (int)cityWidth,
                    (int)(displayWidth/citiesInitXs[i]), (int)cityInitY);
            cities.add(city);
        }


        /*
        ASTEROID SETUP
         */
        asteroidsToUse = new LinkedList<>();
        for(int i = 0 ; i < STARTING_ASTEROID_COUNT ; i++) {
            asteroidsToUse.offer(new Asteroid(this,
                    displayWidth/ASTEROID_WIDTH_PROPORTION,
                    STARTING_ASTEROID_VELOCITY));
        }

        /*
        DISPLAY DATA STRUCTURES SETUP
         */
        missilesToDisplay = new LinkedList<>();
        explosionsToDisplay = new LinkedList<>();
        asteroidsToDisplay = new LinkedList<>();

        /*
        CROSSHAIR SETUP
         */
        crosshair = new Crosshair(this, displayWidth/CROSSHAIR_WIDTH_PROPORTION,
                displayHeight/CROSSHAIR_HEIGHT_PROPORTION,
                displayHeight/CROSSHAIR_OFFSET_PROPORTION,
                displayWidth/CROSSHAIR_OFFSET_PROPORTION);


        /*
        ROUND SCREEN SETUP
         */
        roundScreen = new RoundScreen(this, 0, 3, 0,
                displayWidth/ ROUND_START_TEXT_PROPORTION,
                displayWidth / SCORE_ROUND_TEXT_PROPORTION,
                new PVector(displayWidth/ROUND_START_TEXT_X_PROPORTION, displayHeight/ROUND_START_TEXT_Y_PROPORTION),
                displayWidth/SCORE_X_OFFSET_PROPORTION, displayWidth/ROUND_X_OFFSET_PROPORTION,
                displayHeight/SCORE_ROUND_Y_OFFSET_PROPORTION);
    }

    // before window exists
    public void settings() {
        fullScreen();
    }

    // update and render
    public void draw() {
        background(0);

        roundScreen.draw();
        crosshair.draw();

        if(numAsteroidsThisRound <= 0 && asteroidsToDisplay.isEmpty()
        && missilesToDisplay.isEmpty() && explosionsToDisplay.isEmpty()) {
            roundScreen.newRound();
            for(Ballista b : ballistaSelector) {
                b.resetBallista();
            }
            for(City c : cities) {
                if(c.isDestroyed && roundScreen.cityScore >= 10000) {
                    c.rebuildCity();
                    roundScreen.cityScore -= 10000;
                }
            }
            numAsteroidsThisRound = STARTING_ASTEROID_COUNT+roundScreen.roundNumber*INCREMENT_PER_ROUND;
        }
        if(numAsteroidsThisRound > 0 && roundScreen.hasRoundStarted()) {
            int current_time = (int) (System.currentTimeMillis()/1000);
            if(last_second_checked != current_time) {
                if((rand.nextInt(probability) == 0 || current_time - last_asteroid_spawn > MAX_DELAY_BETWEEN_SPAWNS)
                        && asteroidsToDisplay.size() <= maxScreenAsteroids) {
                    addAsteroidsToDisplay();
                    numAsteroidsThisRound--;
                    last_asteroid_spawn = current_time;
                }
                last_second_checked = current_time;
            }

        }

        int explosionsSize = explosionsToDisplay.size();
        for(int i = 0 ; i < explosionsSize ; i++) {
            Explosion e = explosionsToDisplay.poll();
            if (e != null) {
                if (!e.fullyExploded()) {
                    e.draw();
                    explosionsToDisplay.offer(e);
                } else {
                    explosionsToUse.offer(e);
                }
            }
        }

        for(City c : cities) {
            c.draw();
        }

        ballistaSelector.get(selectedBallista).changeRotation();
        for(Ballista b : ballistaSelector) {
            b.draw();
        }
        int queueSize = missilesToDisplay.size();
        for(int i = 0 ; i < queueSize ; i++) {
            PlayerMissile pm = missilesToDisplay.poll();
            if(pm != null && pm.move()) {
                pm.draw();
                missilesToDisplay.offer(pm);
            }
        }
        int asteroidSize = asteroidsToDisplay.size();
        for(int i = 0 ; i < asteroidSize ; i++) {
            Asteroid a = asteroidsToDisplay.poll();
            if(a != null) {
                int collidedCity;
                int collidedBallista;
                if(a.checkExplosionCollision(explosionsToDisplay)) {
                    addExplosion(a.position.x, a.position.y);
                    roundScreen.destroyedAsteroid();
                } else if((collidedCity = a.checkCityCollision(cities)) != -1){
                        cities.get(collidedCity).isDestroyed = true;
                } else if((collidedBallista = a.checkBallistaCollision(ballistaSelector)) != -1) {
                    ballistaSelector.get(collidedBallista).isDestroyed = true;
                }
                else if(a.move()) {
                    a.draw();
                    asteroidsToDisplay.offer(a);
                } else {
                    asteroidsToUse.offer(a);
                }
            }
        }
    }

    // Read keyboard for input. Notice how booleans are used to maintain state and so
// give a smooth update. These methods are not called often enough otherwise.
    public void keyPressed() {
        // space to fire
        if (key == ' ' && roundScreen.hasRoundStarted()) {
            PlayerMissile pm = ballistaSelector.get(selectedBallista).fire();
            if(pm != null) {
                missilesToDisplay.add(pm);
            }
        }
        if (key == CODED) {
            switch (keyCode) {
                case LEFT:
                    selectedBallista = 0;
                    break;
                case RIGHT:
                    selectedBallista = 2;
                    break;
                case DOWN:
                    selectedBallista = 1;
                    break;
            }
        }
    }

    public void mousePressed() {
        if (missilesToDisplay.size() != 0) {
            PlayerMissile pm = missilesToDisplay.poll();
            addExplosion(pm.position.x, pm.position.y);
        }
    }

    public void addExplosion(float x, float y) {
        Explosion e = explosionsToUse.poll();
        // add new explosion
        if (e == null) {
            e = new Explosion(this, 0, 0, 0,
                    displayWidth / EXPLOSION_RATIO_PROPORTION,
                    displayWidth / EXPLOSION_RADIUS_PROPORTION);
        }
        e.reset(x, y);
        explosionsToDisplay.offer(e);
    }

    public void addAsteroidsToDisplay() {
        Asteroid a = asteroidsToUse.poll();
        if(a == null) {
            a = new Asteroid(this, ASTEROID_WIDTH_PROPORTION, STARTING_ASTEROID_VELOCITY);
        }
        a.reset(rand.nextInt(displayWidth), 0, rand.nextInt(displayWidth));
        asteroidsToDisplay.offer(a);
    }

}