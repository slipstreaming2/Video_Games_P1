import processing.core.PApplet;
import processing.core.PVector;

import ddf.minim.AudioPlayer;

import java.awt.*;
import java.util.*;

public class Shmup extends PApplet {

    public static void main(String[] args) {
        PApplet.main("Shmup");
    }

    final float MISSILE_K1_DRAG_COEF = 0.5f,
            MISSILE_K2_DRAG_COEF = 0.2f;

    final int NUMBER_OF_BALLISTAS = 3;
    final int NUMBER_OF_CITIES = 6;

    final int STARTING_ASTEROID_VELOCITY = 200,
            INCREMENT_ASTEROID_VELOCITY_PROPORTION = 1200,
            ASTEROID_SPLIT_PROBABILITY = 10,
            ASTEROID_SPLIT_MIN_NUM = 2,
            ASTEROID_SPLIT_MAX_NUM = 2,
            ASTEROID_SPLIT_MAX_MAX_NUM = 4,
            ASTEROID_SPLIT_INCREMENT_LEVEL = 10,
            ASTEROID_SPLIT_MIN_Y_PROPORTION = 6,
            ASTEROID_SPLIT_MAX_Y_PROPORTION = 2,
            ASTEROID_SPLIT_X_AMOUNT_PROPORTION = 1,
            ASTEROID_SPLIT_NUDGE_AMOUNT_PROPORTION = 2,
            ASTEROID_SPLIT_LEVEL = 2;

    final int GROUND_HEIGHT_PROPORTION = 20;

    final double STARTING_MAX_TIME_BETWEEN_WAVES = 7,
                STARTING_MIN_TIME_BETWEEN_WAVES = 5,
                DECREASE_TIME_BETWEEN_WAVES = 0.2,
                ABSOLUTE_MIN_TIME_BETWEEN_WAVES = 2,
                ABSOLUTE_MAX_TIME_BETWEEN_WAVES = 4,
                STARTING_ASTEROID_COUNT = 8,
                STARTING_MAX_NUM_ASTEROIDS_PER_WAVE = 2,
                STARTING_MIN_NUM_ASTEROIDS_PER_WAVE = 1,
                INCREMENT_MAX_NUM_ASTEROIDS_PER_WAVE = 0.2,
                INCREMENT_MIN_NUM_ASTEROIDS_PER_WAVE = 0.2,
                INCREMENT_ASTEROIDS_PER_WAVE = 3,
                STARTING_NUM_SATELLITES_BOMBERS = 0,
                INCREMENT_SATELLITES_BOMBERS = 0.4,
                STARTING_MAX_NUM_SATELLITES = 1,
                STARTING_MIN_NUM_SATELLITES = 1,
                INCREMENT_MAX_NUM_SATELLITES = 0.2,
                INCREMENT_MIN_NUM_SATELLITES = 0.2,
                STARTING_NUM_SMART_BOMBS = 0,
                STARTING_MAX_NUM_SMART_BOMBS = 1,
                STARTING_MIN_NUM_SMART_BOMBS = 0,
                INCREMENT_MAX_NUM_SMART_BOMBS = 0.25,
                INCREMENT_MIN_NUM_SMART_BOMBS = 0.5,
                INCREMENT_SMART_BOMBS = 0.3,
                INCREMENT_ASTEROID_VELOCITY = 2,
                INCREMENT_SMART_BOMB_VELOCITY = 1200,
                STARTING_MAX_NUM_OBJECTS = 3,
                INCREMENT_MAX_NUM_OBJECTS = 0.3,
                ABSOLUTE_MAX_NUM_OBJECTS = 10;

    final int GAMEOVER_Y_OFFSET_PROPORTION = 5,
            GAMEOVER_TEXT_SIZE_PROPORTION = 10;

    final float ASTEROID_WEIGHT_KG = 100,
            ASTEROID_K1_DRAG_COEF = 0.5f,
            ASTEROID_K2_DRAG_COEF = 0.2f;

    final int SMART_BOMB_STARTING_COUNT = 5,
            STARTING_SMART_BOMB_VELOCITY = 800;
    final float SMART_BOMB_K1_DRAG_COEF = 0.5f,
            SMART_BOMB_K2_DRAG_COEF = 0.5f;

    final int MS_PER_UPDATE = 20;

    final int REBUILD_CITY_NUM_POINTS = 10000,
            ASTEROID_POINTS = 25,
            SATELLITE_POINTS = 100,
            SMART_BOMB_POINTS = 100,
            SAVED_CITY_POINTS = 100,
            UNUSED_BOMBS_POINTS = 5;

    final float GRAVITY = 0.05f;

    Set<GameObject> toDraw;

    ArrayList<Ballista> ballistaSelector;
    ArrayList<City> cities;

    Set<PlayerMissile> missilesToDisplay;
    Set<Asteroid> asteroidsToDisplay;
    Set<Satellite> satellitesToDisplay;

    Queue<Explosion> explosionsToUse;
    Queue<Asteroid> asteroidsToUse;
    Queue<Satellite> satellitesToUse;
    Queue<SmartBomb> smartBombsToUse;

    Crosshair crosshair;

    RoundScreen roundScreen;
    
    QuadTree quadTree;
    ForceRegistry forceRegistry;
    Gravity gravity;
    Drag missileDrag;
    Drag asteroidDrag;
    Drag smartBombDrag;

    int numAsteroidsThisRound,
            maxSpawnNumAsteroidsThisRound,
            minSpawnNumAsteroidsThisRound,
            numSatellitesThisRound,
            numSmartBombsThisRound,
            lastWaveTime,
            currentWaveTime,
            minSpawnNumSmartBombsThisRound,
            maxSpawnNumSmartBombsThisRound,
            minSpawnNumSatellitesThisRound,
            maxSpawnNumSatellitesThisRound,
            maxSpawnsAtOnce,
            asteroidVelocity,
            smartBombVelocity;

    double maxTimeBetweenWaves,
            minTimeBetweenWaves;

    int selectedBallista = 0;
    int keyPressBallista = 0;
    
    ArrayList<GameObject> toRem;
    
    boolean fired = false;
    boolean mousePressed = false;
    boolean readyToFireAgain = true;
    boolean exitGame = false;
    boolean gameOver = false;

    int explosionsHappening = 0;

    double previous;
    double lag = 0;

    Random rand;

    Factory f;

    ArrayList<AudioPlayer> soundsToPlay;

    /**
     * generates a random time between max and min
     * @param maxTime max time
     * @param minTime min time
     * @return a time between max and min
     */
    public int randGenTime(int maxTime, int minTime) {
        return rand.nextInt((maxTime - minTime) + 1) + minTime;
    }


    /**
     * initialises game object and elements
     */
    public void setup() {
        noStroke();
        rectMode(CENTER);
        imageMode(CENTER);
        textAlign(CENTER);
        rand = new Random();
        lastWaveTime = (int) (System.currentTimeMillis()/1000);
        numAsteroidsThisRound = (int) STARTING_ASTEROID_COUNT;
        maxSpawnNumAsteroidsThisRound = (int) STARTING_MAX_NUM_ASTEROIDS_PER_WAVE;
        minSpawnNumAsteroidsThisRound = (int) STARTING_MIN_NUM_ASTEROIDS_PER_WAVE;
        maxTimeBetweenWaves = STARTING_MAX_TIME_BETWEEN_WAVES;
        minTimeBetweenWaves = STARTING_MIN_TIME_BETWEEN_WAVES;
        numSatellitesThisRound = (int) STARTING_NUM_SATELLITES_BOMBERS;
        numSmartBombsThisRound = (int) STARTING_NUM_SMART_BOMBS;
        currentWaveTime = randGenTime((int) maxTimeBetweenWaves,(int)  minTimeBetweenWaves);
        minSpawnNumSmartBombsThisRound = (int) STARTING_MIN_NUM_SMART_BOMBS;
        maxSpawnNumSmartBombsThisRound = (int) STARTING_MAX_NUM_SMART_BOMBS;
        minSpawnNumSatellitesThisRound = (int) STARTING_MIN_NUM_SATELLITES;
        maxSpawnNumSatellitesThisRound = (int) STARTING_MAX_NUM_SATELLITES;
        maxSpawnsAtOnce = (int) STARTING_MAX_NUM_OBJECTS;
        asteroidVelocity = displayHeight/STARTING_ASTEROID_VELOCITY;
        smartBombVelocity = displayHeight/STARTING_SMART_BOMB_VELOCITY;

        previous = System.currentTimeMillis();

        forceRegistry = new ForceRegistry();
        gravity = new Gravity(new PVector(0f, GRAVITY));
        missileDrag = new Drag(MISSILE_K1_DRAG_COEF, MISSILE_K2_DRAG_COEF);
        asteroidDrag = new Drag(ASTEROID_K1_DRAG_COEF, ASTEROID_K2_DRAG_COEF);
        smartBombDrag = new Drag(SMART_BOMB_K1_DRAG_COEF, SMART_BOMB_K2_DRAG_COEF);
        smartBombsToUse = new LinkedList<>();
        toRem = new ArrayList<>();

        f = new Factory(this);

        soundsToPlay = new ArrayList<>();

        toDraw = new HashSet<>();

        /*
        QuadTree setup
         */
        quadTree = new QuadTree(0, new Rectangle(0,0,displayWidth, displayHeight), this);

        /*
        BALLISTA + EXPLOSIONS SETUP
         */
        ballistaSelector = new ArrayList<>();
        explosionsToUse = new LinkedList<>();

        for(int i = 0 ; i < NUMBER_OF_BALLISTAS ; i++) {
            Ballista ballista = f.createBallista(i);
            if(i == 0) ballista.isSelected = true;
            ballistaSelector.add(ballista);
            toDraw.add(ballista);
            explosionsToUse.offer(f.createExplosion());
        }

        /*
        CITIES SETUP
         */
        cities = new ArrayList<>();
        for(int i = 0 ; i < NUMBER_OF_CITIES ; i++) {
            City city = f.createCity(i);
            cities.add(city);
            toDraw.add(city);
        }

        /*
        ASTEROID SETUP
         */
        asteroidsToUse = new LinkedList<>();
        for(int i = 0 ; i < STARTING_ASTEROID_COUNT ; i++) {
            asteroidsToUse.add(f.createAsteroid(false));
        }

        /*
        DISPLAY DATA STRUCTURES SETUP
         */
        missilesToDisplay = new HashSet<>();
        asteroidsToDisplay = new HashSet<>();
        satellitesToDisplay = new HashSet<>();

        /*
        CROSSHAIR SETUP
         */
        crosshair = f.createCrosshair();
        toDraw.add(crosshair);


        /*
        ROUND SCREEN SETUP
         */
        roundScreen = f.createRoundscreen();

        /*
        SATELLITE SETUP
         */
        satellitesToUse = new LinkedList<>();
        for(int i = 0 ; i < STARTING_MAX_NUM_SATELLITES ; i++) {
            satellitesToUse.add(f.createSatellite(i%2 == 0));
        }

        /*
        SMART BOMB SETUP
        */
        for(int i = 0 ; i < SMART_BOMB_STARTING_COUNT; i++) {
            smartBombsToUse.add((SmartBomb) f.createAsteroid(true));
        }

    }

    /**
     * adds the sound to be played
     * @param sound sound to play
     */
    public void addSound(AudioPlayer sound) {
        soundsToPlay.add(sound);
    }

    /**
     * plays all sounds as needed
     */
    public void playSounds() {
        for(AudioPlayer ap : soundsToPlay) {
            ap.play();
            ap.rewind(); // rewind to use sound again
        }
        soundsToPlay.clear();
    }

    /**
     * adds object to draw
     * @param g object to draw
     */
    public void addToDraw(GameObject g) {
        toDraw.add(g);
    }

    /**
     * removes object from draw
     * @param g object to remove from draw
     */
    public void removeFromDraw(GameObject g) {
        toDraw.remove(g);
    }

    // before window exists
    public void settings() {
        fullScreen();
    }

    /**
     * renders and draws the objects
     */
    public void render() {
        background(0);
        if(!gameStarted) {
            startScreenRender();
            return;
        }
//        image(backgroundImage, displayWidth/2, displayHeight/2, displayWidth, displayHeight);
        if(gameOver) {
            gameOverRender();
            if(exitGame) exit();
            return;
        }
        // THE GROUND
        fill(0,123,0);
        rect(displayWidth/2, displayHeight-(displayHeight/GROUND_HEIGHT_PROPORTION)/2, displayWidth,
                displayHeight/GROUND_HEIGHT_PROPORTION);

        roundScreen.draw();
//        quadTree.draw();
//        text(frameRate, displayWidth/90, displayHeight/55);
        for(GameObject g : toDraw) {
            g.draw();
        }

    }

    /**
     * moves all the objects
     */
    public void moveObjects() {
        forceRegistry.updateForces();
        for(GameObject g : toDraw) {
            if(g instanceof Moveable) {
                ((Moveable) g).move();
            } else if(g instanceof Explosion) {
                ((Explosion) g).expand();
            } else if(g instanceof Ballista) {
                // changes rotation of ballista to point at cursor
                ((Ballista) g).changeRotation();
            }
        }
    }

    /**
     * inserts all collidable object into quadtree to detect collisions
     */
    public void insertAllIntoQuadTree() {
        for(GameObject g : toDraw) {
            if(g instanceof Collidable) {
                quadTree.insert((Collidable) g);
            }
        }
    }

    /**
     * removes objects that are out of bounds or explosions that are done exploding
     */
    public void checkAllBounds() {
       toRem.clear();
       for(GameObject g : toDraw) {
           if(g instanceof Moveable && ((Moveable)g).notInBounds()) {
               toRem.add(g);
           } else if(g instanceof Explosion && ((Explosion) g).fullyExploded()) {
               toRem.add(g);
               explosionsHappening--;
           } 
       }

       // removes objects from toDraw, but also reuses the objects
       for(GameObject g : toRem) {
           toDraw.remove(g);
           if (g instanceof Explosion) {
               explosionsToUse.add((Explosion) g);
           } else if (g instanceof Satellite) {
               satellitesToUse.add((Satellite) g);
               satellitesToDisplay.remove((Satellite) g);
           } else {
               // removes objects from physics computation
               forceRegistry.remove((PhysicsObject) g);
               if (g instanceof Asteroid) {
                   asteroidsToUse.add((Asteroid) g);
                   asteroidsToDisplay.remove((Asteroid) g);
               } else if(g instanceof PlayerMissile) {
                   missilesToDisplay.remove((PlayerMissile) g);
               }
           }
       }
    }

    /**
     * determines if a round has ended, and if it has handling this
     */
    public void checkEndOfRound() {
        /*
        Round has ended, reset ballistas, cities if we have enough score
        */
        if(numAsteroidsThisRound+numSatellitesThisRound+numSmartBombsThisRound <= 0 && asteroidsToDisplay.isEmpty()
                && missilesToDisplay.isEmpty() && satellitesToDisplay.isEmpty() && explosionsHappening == 0) {
            roundScreen.newRound();
            for(Ballista b : ballistaSelector) {
                if(!b.isDestroyed) roundScreen.addScore(UNUSED_BOMBS_POINTS*b.missiles.size());
                b.resetBallista();
            }
            for(City c : cities) {
                if(!c.isDestroyed) {
                    roundScreen.addScore(SAVED_CITY_POINTS);
                }
            }
            for(City c : cities) {
                if(c.isDestroyed && roundScreen.cityScore >= REBUILD_CITY_NUM_POINTS) {
                    c.rebuildCity();
                    roundScreen.cityScore -= REBUILD_CITY_NUM_POINTS;
                }
            }
            numAsteroidsThisRound = (int) (STARTING_ASTEROID_COUNT +
                    roundScreen.roundNumber*INCREMENT_ASTEROIDS_PER_WAVE);
            minSpawnNumAsteroidsThisRound += (int) (STARTING_MIN_NUM_ASTEROIDS_PER_WAVE +
                    roundScreen.roundNumber*INCREMENT_MIN_NUM_ASTEROIDS_PER_WAVE);
            maxSpawnNumAsteroidsThisRound += (int) (STARTING_MAX_NUM_ASTEROIDS_PER_WAVE +
                    roundScreen.roundNumber*INCREMENT_MAX_NUM_ASTEROIDS_PER_WAVE);
            numSatellitesThisRound = (int) (STARTING_NUM_SATELLITES_BOMBERS +
                    roundScreen.roundNumber*INCREMENT_SATELLITES_BOMBERS);
            numSmartBombsThisRound = (int) (STARTING_NUM_SMART_BOMBS+ roundScreen.roundNumber*INCREMENT_SMART_BOMBS);
            maxSpawnNumAsteroidsThisRound += INCREMENT_MAX_NUM_ASTEROIDS_PER_WAVE;
            minSpawnNumAsteroidsThisRound += INCREMENT_MIN_NUM_ASTEROIDS_PER_WAVE;
            maxSpawnNumSatellitesThisRound += INCREMENT_MAX_NUM_SATELLITES;
            minSpawnNumSatellitesThisRound += INCREMENT_MIN_NUM_SATELLITES;
            maxSpawnNumSmartBombsThisRound += INCREMENT_MAX_NUM_SMART_BOMBS;
            minSpawnNumSmartBombsThisRound += INCREMENT_MIN_NUM_SMART_BOMBS;
            asteroidVelocity += displayHeight/INCREMENT_ASTEROID_VELOCITY_PROPORTION;
            smartBombVelocity += displayHeight/INCREMENT_SMART_BOMB_VELOCITY;
            minTimeBetweenWaves -= DECREASE_TIME_BETWEEN_WAVES;
            minTimeBetweenWaves = Math.max(minTimeBetweenWaves, ABSOLUTE_MIN_TIME_BETWEEN_WAVES);
            maxTimeBetweenWaves -= DECREASE_TIME_BETWEEN_WAVES;
            maxTimeBetweenWaves = Math.max(maxTimeBetweenWaves, ABSOLUTE_MAX_TIME_BETWEEN_WAVES);
            maxSpawnsAtOnce += INCREMENT_MAX_NUM_OBJECTS;
            maxSpawnsAtOnce = Math.min(maxSpawnsAtOnce, (int) ABSOLUTE_MAX_NUM_OBJECTS);
        }
    }

    /**
     * adds a new wave of enemies to battle
     */
    public void addNewWave() {
        // have enemies to add
        if(numAsteroidsThisRound > 0 || numSatellitesThisRound > 0 || numSmartBombsThisRound > 0) {
            int current_time = (int) (System.currentTimeMillis() / 1000);
            // NEW WAVE STARTING, or all asteroids and satellites gone
            if (current_time - lastWaveTime >= currentWaveTime ||
                    (asteroidsToDisplay.isEmpty() && satellitesToDisplay.isEmpty() && explosionsHappening == 0)) {
                int numAsteroids = 0;
                if (numAsteroidsThisRound > 0)
                    numAsteroids = randGenTime(Math.min(maxSpawnNumAsteroidsThisRound, maxSpawnsAtOnce), Math.min(minSpawnNumAsteroidsThisRound, maxSpawnsAtOnce));
                int numBombers = 0;
                if (numSatellitesThisRound > 0)
                    numBombers = randGenTime(Math.min(maxSpawnNumSatellitesThisRound, maxSpawnsAtOnce - numAsteroids),
                            Math.min(minSpawnNumSatellitesThisRound, maxSpawnsAtOnce - numAsteroids));
                int numSmartBombs = 0;
                if (numSmartBombsThisRound > 0)
                    numSmartBombs = randGenTime(Math.min(maxSpawnNumSmartBombsThisRound, maxSpawnsAtOnce - numAsteroids - numBombers),
                            Math.min(minSpawnNumSmartBombsThisRound, maxSpawnsAtOnce - numAsteroids - numBombers));
                for (int i = 0; i < numAsteroids; i++) {
                    addAsteroids(-1, -1, null, false, true);
                    numAsteroidsThisRound--;
                }
                if(numBombers > 0) {
                    addSatelliteToDisplay();
                    numSatellitesThisRound--;
                }
                for (int i = 0; i < numSmartBombs; i++) {
                    addAsteroids(-1, -1, null, true, true);
                    numSmartBombsThisRound--;
                }
                lastWaveTime = current_time;
                currentWaveTime = randGenTime((int)maxTimeBetweenWaves, (int)minTimeBetweenWaves);
            }
        }
    }


    /**
     * performs mostly physics computation and collision detection
     */
    public void compute() {
        if(gameOver || !gameStarted) return;
        gameOver = cities.stream().allMatch(c -> c.isDestroyed);
        if(gameOver) return;

        if(!roundScreen.hasRoundStarted()) {
            roundScreen.computeRoundStart();
        }
        checkEndOfRound();

        if(roundScreen.hasRoundStarted()) {
            quadTree.clear();
            moveObjects();
            checkAllBounds(); // if objects out of bounds, do not check collision
            insertAllIntoQuadTree();
            quadTree.checkCollisions(); // check collisions after moving before render, avoids overlap confusion (theoretically)
            
            addNewWave(); // adds new wave of enemies

            // drop satellite asteroids
            for(Satellite s : satellitesToDisplay) {
                if(s.dropAsteroid()) {
                    addAsteroids((int) s.position.x, (int) s.position.y,
                            s.getVector().copy().mult(ASTEROID_WEIGHT_KG), false, true);
                    numAsteroidsThisRound--;
                }
            }
            splitAsteroids(); // split asteroids potentially
        }
    }

    /**
     * determines if asteroids are splitting, then creates splitting asteroids
     */
    public void splitAsteroids() {
        // at appropriate round level to start splitting
        if(roundScreen.roundNumber > ASTEROID_SPLIT_LEVEL) {
            ArrayList<Asteroid> toAdd = new ArrayList<>();
            for (Asteroid a : asteroidsToDisplay) {
                if (!(a instanceof SmartBomb)) { // smart bombs do not split
                    // asteroid will split, has not split yet, and the position is past the splitting point
                    if (a.willSplit && !a.hasSplit && a.position.y > displayHeight / ASTEROID_SPLIT_MIN_Y_PROPORTION) {
                        a.hasSplit = true;
                        // determine number to split into
                        int numSplit = randGenTime(roundScreen.roundNumber > ASTEROID_SPLIT_INCREMENT_LEVEL ?
                                ASTEROID_SPLIT_MAX_MAX_NUM : ASTEROID_SPLIT_MAX_NUM, ASTEROID_SPLIT_MIN_NUM);
                        PVector v = a.getVelocity();
                        for (int i = 0; i < numSplit; i++) {
                            // if more than two, shove these slightly further out
                            int nudgeAmount = i > 2 ? (int) (displayWidth/ASTEROID_SPLIT_X_AMOUNT_PROPORTION * 1.05 ):
                                    displayWidth/ASTEROID_SPLIT_X_AMOUNT_PROPORTION;
                            // vector to shove out by
                            PVector force = new PVector(nudgeAmount-a.position.x, displayHeight-a.position.y);
                            force.normalize();
                            force.x *= i % 2 == 0 ? -1 : 1; // alternate between left and right
                            force.mult(v.mag()); // get back up to speed of asteroid splitting from
                            toAdd.add(addAsteroids((int) a.position.x, (int) a.position.y, force,
                                    false, false));
                        }

                    }
                }
            }
            toDraw.addAll(toAdd);
            asteroidsToDisplay.addAll(toAdd);
        }
    }

    /**
     * handles input by the user
     */
    public void handleInput() {
        if(mousePressed) {
            // explode all missiles
            for(PlayerMissile pm : missilesToDisplay) {
                forceRegistry.remove(pm);
                addExplosion(pm.position.x, pm.position.y);
                toDraw.remove(pm);
            }
            missilesToDisplay.clear();
            mousePressed = false;
        }
        if(fired) { // handles firing a missile
            PlayerMissile pm = ballistaSelector.get(selectedBallista).fire();
            if(pm != null) {
                missilesToDisplay.add(pm);
                toDraw.add(pm);
                forceRegistry.add(pm, gravity);
                forceRegistry.add(pm, missileDrag);
            }
            fired = false;
        }
        // change the selected ballista
        if(selectedBallista != keyPressBallista) {
            ballistaSelector.get(selectedBallista).isSelected = false;
            selectedBallista = keyPressBallista;
            ballistaSelector.get(selectedBallista).isSelected = true;
        }
    }

    boolean gameStarted = false;
    public void startScreenRender() {
        fill(125,125,0);
        textSize(displayHeight/GAMEOVER_TEXT_SIZE_PROPORTION);
        text("BALLISTA COMMAND", displayWidth/2, displayHeight/2);
        text("Press any key to start!", displayWidth/2, displayHeight/2 + displayHeight/(GAMEOVER_Y_OFFSET_PROPORTION *2));
    }

    /**
     * render gameover
     */
    public void gameOverRender() {
        textSize(displayHeight/GAMEOVER_TEXT_SIZE_PROPORTION);
        fill(255);
        text("Game Over! Press any key to exit", displayWidth/2, displayHeight/2);
        text("Wave reached: " + roundScreen.roundNumber, displayWidth/2, displayHeight/2 + displayHeight/(GAMEOVER_Y_OFFSET_PROPORTION *2));
        text("Score: " + roundScreen.score, displayWidth/2, displayHeight/2 + displayHeight/ GAMEOVER_Y_OFFSET_PROPORTION);
    }



    /**
     * game loop to update and render
     * deals with lag per game loop to ensure smooth gameplay
     */
    public void draw() {
        double current = System.currentTimeMillis();
        double elapsed = current - previous;
        previous = current;
        lag += elapsed;
        handleInput();
        while(lag >= MS_PER_UPDATE) {
            compute();
            lag -= MS_PER_UPDATE;
        }
        playSounds(); // play game sounds if there are any to play
        render();
    }


    /**
     * reads input from the user, note uses boolean flags to then be used in handleInput for proper game loop control
     */
    public void keyPressed() {
        if(gameOver) exitGame = true;
        if(!gameStarted) gameStarted = true;
        // space to fire, but only after releasing spacebar
        if (key == ' ' && readyToFireAgain && roundScreen.hasRoundStarted()) {
            fired = true;
            readyToFireAgain = false;
        }
        // select ballista
        switch(key) {
            case 'a':
                keyPressBallista = 0;
                break;
            case 's':
                keyPressBallista = 1;
                break;
            case 'd':
                keyPressBallista = 2;
                break;
        }
    }

    /**
     * release sparebar to fire again
     */
    public void keyReleased() {
        if (key == ' ') {
            readyToFireAgain = true;
        }
    }

    /**
     * pressed the mouse to explode everything
     */
    public void mousePressed() {
        mousePressed = true;
    }

    /**
     * adds in an explosion at coord specified
     * @param x x coord
     * @param y y coord
     */
    public void addExplosion(float x, float y) {
        Explosion e = explosionsToUse.poll();
        // add new explosion
        if (e == null) {
            e = f.createExplosion();
        }
        e.reset(x, y);
        toDraw.add(e);
        explosionsHappening++;
    }

    /**
     * adds in an asteroid or smartbomb at coord
     * @param x x coord
     * @param y y coord
     * @param toAdd whether adding in any force to the asteroid
     * @param isSmartBomb whether it is a smart bomb
     * @param addingToDisplay whether we are immediately putting into display, used when an asteroid splits
     * @return asteroid/smartbomb to use for splitting asteroids only
     */
    public Asteroid addAsteroids(int x, int y, PVector toAdd, boolean isSmartBomb, boolean addingToDisplay) {
        Asteroid a = isSmartBomb ? smartBombsToUse.poll() : asteroidsToUse.poll();
        if(a == null) {
            a = f.createAsteroid(isSmartBomb);
        }
        int targetX;
        // not dropped from a satellite
        if(toAdd == null) {
            x = rand.nextInt(displayWidth);
            y = 0;
            targetX = rand.nextInt(displayWidth);
        } else { // dropped from satellite or from splitting asteroid
            targetX = -1;
            if(addingToDisplay) a.addForce(toAdd);
            else a.velocity = toAdd;
        }
        // asteroid will split if not dropped from a satellite and not split already
        a.reset(x, y, targetX, x != -1 && addingToDisplay && rand.nextInt(ASTEROID_SPLIT_PROBABILITY) == 0);
        if(addingToDisplay) {
            toDraw.add(a);
            asteroidsToDisplay.add(a);
        }
        // add forces that act upon asteroid
        forceRegistry.add(a, gravity);
        forceRegistry.add(a, isSmartBomb ? smartBombDrag : asteroidDrag);
        return a;
    }

    /**
     * remove asteroid from display due to explosion
     * @param a asteroid to remove
      */
    public void destroyAsteroidFromDisplay(Asteroid a) {
        toDraw.remove(a);
        asteroidsToDisplay.remove(a);
        if(a instanceof SmartBomb) {
            roundScreen.addScore(SMART_BOMB_POINTS);
            smartBombsToUse.offer((SmartBomb) a);
        }
        else {
            roundScreen.addScore(ASTEROID_POINTS);
            asteroidsToUse.add(a);
        }
        forceRegistry.remove(a);
    }

    /**
     * destroys satellite from display ie. explosion
     * @param s satellite to remove
     */
    public void destroySatelliteFromDisplay(Satellite s) {
        toDraw.remove(s);
        satellitesToDisplay.remove(s);
        roundScreen.addScore(SATELLITE_POINTS);
        satellitesToUse.add(s);
    }

    /**
     * adds a satellite to the display
     */
    public void addSatelliteToDisplay() {
        Satellite s = satellitesToUse.poll();
        if(s == null) {
            s = f.createSatellite(rand.nextInt(2) == 0);
        }
        s.reset();
        satellitesToDisplay.add(s);
        toDraw.add(s);
    }

}