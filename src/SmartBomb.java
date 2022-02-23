import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;
import ddf.minim.*;

import java.util.HashSet;
import java.util.Set;

/**
 * smart bomb, can dodge explosions to an extent
 */
public class SmartBomb extends Asteroid {
    int actualRadius; // the real radius of the smart bomb
    int nudgeAmount;
    AudioPlayer spawnSound;
    Set<Explosion> encounteredBefore; // do not move twice for same explosion

    /**
     * constructor
     * @param app app
     * @param dodgeRadius the radius the smart bomb detects explosion at
     * @param mass mass of the smart bomb
     * @param actualRadius the real radius of the smart bomb
     * @param nudgeAmount the amount to dodge by
     * @param img image
     * @param spawnSound sound
     */
    public SmartBomb(PApplet app, int dodgeRadius, float mass,
                     int actualRadius, int nudgeAmount, PImage img, AudioPlayer spawnSound) {
        super(app, dodgeRadius, mass, img, false, 0);
        this.actualRadius = actualRadius;
        this.nudgeAmount = nudgeAmount;
        this.spawnSound = spawnSound;
        encounteredBefore = new HashSet<>();
    }

    /**
     * resets the smart bomb, object reuse
     * @param x x coord
     * @param y y coord
     * @param targetX the target x coord to target vector
     * @param isSplitting ignored
     */
    @Override
    void reset(int x, int y, int targetX, boolean isSplitting) {
        ((Shmup) app).addSound(spawnSound); // spawn in, add sound
        encounteredBefore.clear();
        position.x = x;
        position.y = y;
        velocity.x = x - targetX;
        velocity.y = y - app.displayHeight;
        velocity.normalize();
        velocity.mult(-((Shmup) app).smartBombVelocity);
    }

    /**
     * draw smart bomb
     */
    @Override
    public void draw() {
        app.image(img, position.x, position.y, actualRadius*2, actualRadius*2);
    }

    /**
     * handles collision. Note collision is initially just the detection radius, then the actual
     * collision is determined to see if the object is destroyed
     * @param element colliding with
     * @return true if handled
     */
    @Override
    public boolean handleCollision(Collidable element) {
        if(element instanceof Explosion) { // detected explosion, may not have actually collided
            Explosion e = (Explosion) element;
            if(this.circleCircleCollision(this, e, true)) { // check real radius ie. a real collision
                if(!isDestroyed) {
                    ((Shmup) app).addExplosion(position.x, position.y);
                    ((Shmup) app).destroyAsteroidFromDisplay(this);
                    isDestroyed = true;
                }
            } else { // just detected an explosion, dodge the explosion
                if(!encounteredBefore.contains(e)) { // have not dodged this explosion
                    PVector direction = new PVector(position.x - e.position.x, position.y - e.position.y);
                    direction.normalize();
                    direction.mult(nudgeAmount); // move away from blast
                    addForce(direction);
                    encounteredBefore.add(e);
                }
            }
            return true;
        }
        return false;
    }
}