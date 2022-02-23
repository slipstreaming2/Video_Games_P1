import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;

import java.util.*;
import ddf.minim.AudioPlayer;

/**
 * satellite and bomber enemy, not affected by physics but moveable and collidable
 */
public class Satellite extends Moveable implements Collidable {
    Random rand;
    int lastDropTime;
    int currentDropTime;
    PVector vector;
    PImage img;
    AudioPlayer spawnSound;
    boolean isBomber;
    boolean isDestroyed;
    /*
    MOVE THIS OUT TO SEP CONTROLLER/STATE CLASS
    */
    int ySpawnMax;
    int ySpawnMin;
    int minTimeBetweenDrops;
    int maxTimeBetweenDrops;

    /**
     * constructor
     * @param app app
     * @param height height
     * @param width width
     * @param moveIncrement amount to move
     * @param minTimeBetweenDrops minimum time in seconds between drops
     * @param maxTimeBetweenDrops max time in seconds between drops
     * @param ySpawnMax max y it can spawn at
     * @param ySpawnMin min y it can spawn at
     * @param img image
     * @param spawnSound spawn in sound
     * @param isBomber whether it is a bomber or satellite
     */
    public Satellite(PApplet app, int height, int width, int moveIncrement, int minTimeBetweenDrops,
                     int maxTimeBetweenDrops, int ySpawnMax, int ySpawnMin, PImage img, AudioPlayer spawnSound,
                     boolean isBomber) {
        super(app, new PVector(0,0), !isBomber, height, isBomber ? height : width);
        vector = new PVector(moveIncrement, 0);
        this.minTimeBetweenDrops = minTimeBetweenDrops;
        this.maxTimeBetweenDrops = maxTimeBetweenDrops;
        this.ySpawnMax = ySpawnMax;
        this.ySpawnMin = ySpawnMin;
        rand = new Random();
        this.img = img;
        this.spawnSound = spawnSound;
        this.isBomber = isBomber;
        isDestroyed = false;
    }

    /**
     * determines if dropping asteroid
     * @return true if dropping asteroid
     */
    public boolean dropAsteroid() {
        int current_time = (int)(System.currentTimeMillis()/1000);
        if(current_time - lastDropTime >= currentDropTime) {
            lastDropTime = current_time;
            calcDropTime();
            return true;
        }
        return false;
    }

    /**
     * calcs next drop time
     */
    public void calcDropTime() {
        currentDropTime = rand.nextInt((maxTimeBetweenDrops - minTimeBetweenDrops)
                + 1) + minTimeBetweenDrops;
    }

    /**
     * resets the satellite, chooses y coord to start at and resets the drop time
     */
    public void reset() {
        isDestroyed = false;
        ((Shmup) app).addSound(spawnSound);
        int y = rand.nextInt((ySpawnMax - ySpawnMin) + 1) + ySpawnMin;
        lastDropTime = (int)(System.currentTimeMillis()/1000);
        calcDropTime();
        position.x = 0;
        position.y = y;
    }

    /**
     * vector for determining asteroid momentum
     * @return vector of satellite
     */
    public PVector getVector() {
        return vector;
    }

    /**
     * moves satellite
     */
    void move() {
        position.add(vector);
    }

    /**
     * draws the satellite or bomber
     */
    void draw() {
        if(isBomber) app.image(img, position.x, position.y, width, height);
        else {
            // draws an inspiration of Sputnik, N.B. this is not to say Sputnik was evil, it was
            // an easy satellite to draw and so was chosen
            app.fill(255);
            app.stroke(255);
            app.line(position.x, position.y, position.x-height*2, position.y);
            app.line(position.x, position.y, position.x-height*2, position.y-height);
            app.line(position.x, position.y, position.x-height*2, position.y-height*2);
            app.fill(255);
            app.circle(position.x, position.y, height*2);
        }
    }

    /**
     * handles collision against explosion
     * @param element element to handle collision against
     * @return true if handled
     */
    @Override
    public boolean handleCollision(Collidable element) {
        if (element instanceof Explosion) {
            if(!isDestroyed) {
                ((Shmup) app).addExplosion(position.x, position.y);
                ((Shmup) app).destroySatelliteFromDisplay(this);
                isDestroyed = true;
                return true;
            }
        }
        return false;
    }
}