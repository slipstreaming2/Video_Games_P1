import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.Random;
import java.util.Stack;

/**
 * Factory to create all the objects
 */
public class Factory {
    final int MISSILE_WIDTH_PROPORTION = 100;

    final float MISSILE_WEIGHT_KG = 100,
            MISSILE_INCREMENT_PROPORTION = 0.05f;

    final int NUM_MISSILE_PER_BALLISTA = 10;

    final int CROSSHAIR_WIDTH_PROPORTION = 100,
            CROSSHAIR_HEIGHT_PROPORTION = 100,
            CROSSHAIR_OFFSET_PROPORTION = 50;

    final double BALLISTA_HEIGHT_PROPORTION = 10,
            BALLISTA_WIDTH_PROPORTION = 55.0/5.0,
            BALLISTA_LEFT_INIT_X_PROPORTION = 110.0/11.0,
            BALLISTA_MIDDLE_INIT_X_PROPORTION = 2,
            BALLISTA_RIGHT_INIT_X_PROPORTION = 110.0/99.0;

    final double CITY_HEIGHT_PROPORTION = 10,
            CITY_WIDTH_PROPORTION = 55.0/3.0,
            CITY_LEFT_1_INTI_X_PROPORTION = 110.0/23.0,
            CITY_LEFT_2_INIT_X_PROPORTION = 110.0/33.0,
            CITY_LEFT_3_INIT_X_PROPORTION = 110.0/43.0,
            CITY_RIGHT_1_INIT_X_PROPORTION = 110.0/67.0,
            CITY_RIGHT_2_INIT_X_PROPORTION = 110.0/77.0,
            CITY_RIGHT_3_INIT_X_PROPORTION = 110.0/87.0;

    final int EXPLOSION_RATIO_PROPORTION = 1000,
            EXPLOSION_RADIUS_PROPORTION = 50;

    final int TEXT_SIZE_PROPORTION = 17,
            BALLISTA_TEXT_OFFSET_PROPORTION = 11;
    final double BALLIST_PLATFORM_Y_PROPORTION = 13.5;

    final int ASTEROID_WIDTH_PROPORTION = 50,
            ASTEROID_SPLIT_PROBABILITY = 10,
            NUM_ASTEROID_TRAILS = 35; // number of circles to use to represent the asteroid tail

    final float ASTEROID_WEIGHT_KG = 100;

    final int SMART_BOMB_WIDTH_PROPORTION = 100,
            SMART_BOMB_NUDGE_PROPORTION = 2,
            SMART_BOMB_DODGE_RADIUS_PROPORTION = 30;
    final float SMART_BOMB_WEIGHT_KG = 200;

    final int ROUND_START_TEXT_PROPORTION = 30,
            ROUND_START_TEXT_X_PROPORTION = 3,
            ROUND_START_TEXT_Y_PROPORTION = 2,
            SCORE_ROUND_TEXT_PROPORTION = 50,
            SCORE_X_OFFSET_PROPORTION = 10,
            ROUND_X_OFFSET_PROPORTION = 3,
            SCORE_ROUND_Y_OFFSET_PROPORTION = 10;

    final int SATELLITE_MAX_TIME_BETWEEN_DROPS = 6,
            SATELLITE_MIN_TIME_BETWEEN_DROPS = 3,
            SATELLITE_RADIUS_PROPORTION = 50,
            BOMBER_WIDTH_PROPORTOIN = 10,
            BOMBER_HEIGHT_PROPORTION = 10,
            SATELLITE_MOVE_INCREMENT_PROPORTION = 500;
    final double SATELLITE_Y_SPAWN_MAX_PROPORTION = 2,
            SATELLITE_Y_SPAWN_MIN_PROPORTOIN = 4;

    PApplet app;
    Random rand;

    PImage cityImage;
    PImage ballistaImage;
    PImage smartBombImage;
    PImage asteroidImage;
    PImage backgroundImage;
    PImage satelliteImage;

    AudioPlayer cityDestroyedSound,
            smartBombSpawnSound,
            backgroundSound,
            shotSound,
            lowAmmoSound,
            explosionSound,
            satelliteSpawnSound;

    Minim minim;

    double[] citiesInitXs;
    double cityHeight, cityWidth,
            cityInitY;

    double ballistaWidth, ballistaHeight,
            ballistaInitY;

    double[] ballistaInitXs;

    /**
     * constructor of factory
     * @param app app for displayWidth and displayHeight
     */
    public Factory(PApplet app) {
        this.app = app;
        rand = new Random();
        /*
        IMAGE INITIALISATION
         */
        cityImage = app.loadImage("./images/city.png");
        asteroidImage = app.loadImage("./images/asteroid.png");
        ballistaImage = app.loadImage("./images/ballista.png");
        backgroundImage = app.loadImage("./images/background.png");
        satelliteImage = app.loadImage("./images/satellite.png");
        smartBombImage = app.loadImage("./images/smart_bomb.png");

        /*
        SOUND INITIALISATION
         */
        minim = new Minim(app);
        cityDestroyedSound = minim.loadFile("./sound/city_destroyed.wav");
        smartBombSpawnSound = minim.loadFile("./sound/smart_bomb.wav");
        backgroundSound = minim.loadFile("./sound/background.wav");
        lowAmmoSound = minim.loadFile("./sound/low_ammo.wav");
        explosionSound = minim.loadFile("./sound/explosion.wav");
        shotSound = minim.loadFile("./sound/shot.wav");
        satelliteSpawnSound = minim.loadFile("./sound/satellite_spawn.wav");

        citiesInitXs = new double[] {CITY_LEFT_1_INTI_X_PROPORTION,
                CITY_LEFT_2_INIT_X_PROPORTION, CITY_LEFT_3_INIT_X_PROPORTION,
                CITY_RIGHT_1_INIT_X_PROPORTION, CITY_RIGHT_2_INIT_X_PROPORTION,
                CITY_RIGHT_3_INIT_X_PROPORTION};

        cityHeight = app.displayHeight/CITY_HEIGHT_PROPORTION;
        cityWidth = app.displayWidth/CITY_WIDTH_PROPORTION;
        cityInitY = app.displayHeight - cityHeight;

        ballistaWidth = app.displayWidth / BALLISTA_WIDTH_PROPORTION;
        ballistaHeight = app.displayHeight / BALLISTA_HEIGHT_PROPORTION;
        ballistaInitY = app.displayHeight - ballistaHeight;

        ballistaInitXs = new double[]{BALLISTA_LEFT_INIT_X_PROPORTION,
                BALLISTA_MIDDLE_INIT_X_PROPORTION,
                BALLISTA_RIGHT_INIT_X_PROPORTION};
    }

    /**
     * creates an asteroid or smartbomb
     * @param smartBomb whether we want a smart bomb
     * @return asteroid/smartbomb
     */
    public Asteroid createAsteroid(boolean smartBomb) {
        Asteroid a;
        if(smartBomb) {
            a = new SmartBomb(app, app.displayWidth / SMART_BOMB_DODGE_RADIUS_PROPORTION + app.displayWidth / SMART_BOMB_WIDTH_PROPORTION,
                    SMART_BOMB_WEIGHT_KG,app.displayWidth / SMART_BOMB_WIDTH_PROPORTION,
                    app.displayWidth / SMART_BOMB_NUDGE_PROPORTION, smartBombImage, smartBombSpawnSound);
        } else {
            a = new Asteroid(app,
                    app.displayWidth/ASTEROID_WIDTH_PROPORTION, ASTEROID_WEIGHT_KG, asteroidImage,
                    rand.nextInt(ASTEROID_SPLIT_PROBABILITY) == 0, NUM_ASTEROID_TRAILS);
        }
        return a;
    }

    /**
     * creates the satellite
     * @param isBomber whether we want a bomber graphic
     * @return satellite/bomber
     */
    public Satellite createSatellite(boolean isBomber) {
        return new Satellite(app, isBomber ? app.displayHeight/BOMBER_HEIGHT_PROPORTION : app.displayWidth/SATELLITE_RADIUS_PROPORTION,
                isBomber ? app.displayWidth/BOMBER_WIDTH_PROPORTOIN : app.displayWidth/SATELLITE_RADIUS_PROPORTION,
                app.displayWidth/SATELLITE_MOVE_INCREMENT_PROPORTION, SATELLITE_MIN_TIME_BETWEEN_DROPS,
                SATELLITE_MAX_TIME_BETWEEN_DROPS, (int) (app.displayHeight/SATELLITE_Y_SPAWN_MAX_PROPORTION),
                (int) (app.displayHeight/SATELLITE_Y_SPAWN_MIN_PROPORTOIN), satelliteImage,
                satelliteSpawnSound, isBomber);
    }

    /**
     * creates a ballista from index i
     * @param i index i ballista, used for initXs array
     * @return new Ballista
     */
    public Ballista createBallista(int i) {
        Stack<PlayerMissile> missiles = new Stack<>();
        for (int j = 0; j < NUM_MISSILE_PER_BALLISTA; j++) {
            missiles.push(createMissile());
        }
        return new Ballista(app, (int) (app.displayWidth / ballistaInitXs[i]),
                (int) ballistaInitY, (int) ballistaHeight, (int) ballistaWidth, missiles,
                app.displayHeight/TEXT_SIZE_PROPORTION,
                app.displayHeight/BALLISTA_TEXT_OFFSET_PROPORTION,
                ballistaImage, lowAmmoSound, shotSound, cityDestroyedSound,
                (int) (app.displayHeight/BALLIST_PLATFORM_Y_PROPORTION));
    }

    /**
     * Creates a player missile
     * @return new player missile
     */
    public PlayerMissile createMissile() {
        PlayerMissile pm = new PlayerMissile(0, 0,
                app.displayWidth / MISSILE_WIDTH_PROPORTION,
                MISSILE_INCREMENT_PROPORTION, app,
                MISSILE_WEIGHT_KG);
        return pm;
    }

    /**
     * creates an explosion
     * @return an explosion
     */
    public Explosion createExplosion() {
        return new Explosion(app, 0, 0, 0,
                app.displayWidth/EXPLOSION_RATIO_PROPORTION,
                app.displayWidth/EXPLOSION_RADIUS_PROPORTION,
                explosionSound);
    }

    /**
     * creates a city for index i of cityInitXs
     * @param i index i of cityInitXs
     * @return new city
     */
    public City createCity(int i) {
        return new City(app, (int)cityHeight, (int)cityWidth,
                (int)(app.displayWidth/citiesInitXs[i]), (int)cityInitY, cityImage,
                cityDestroyedSound);
    }

    /**
     * creates a new crosshair
     * @return new crosshair
     */
    public Crosshair createCrosshair() {
        return new Crosshair(app, app.displayWidth/CROSSHAIR_WIDTH_PROPORTION,
                app.displayHeight/CROSSHAIR_HEIGHT_PROPORTION,
                app.displayHeight/CROSSHAIR_OFFSET_PROPORTION,
                app.displayWidth/CROSSHAIR_OFFSET_PROPORTION);
    }

    /**
     * creates a new roundscreen
     * @return new roundscreen
     */
    public RoundScreen createRoundscreen() {
        return new RoundScreen(app, 1, 2, 0,
                app.displayWidth/ ROUND_START_TEXT_PROPORTION,
                app.displayWidth / SCORE_ROUND_TEXT_PROPORTION,
                new PVector(app.displayWidth/ROUND_START_TEXT_X_PROPORTION, app.displayHeight/ROUND_START_TEXT_Y_PROPORTION),
                app.displayWidth/SCORE_X_OFFSET_PROPORTION, app.displayWidth/ROUND_X_OFFSET_PROPORTION,
                app.displayHeight/SCORE_ROUND_Y_OFFSET_PROPORTION);
    }
    
    
}