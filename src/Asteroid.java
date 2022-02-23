import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Asteroid class, is an object affected by physics and can be collided with
 */
public class Asteroid extends PhysicsObject implements Collidable {
    PImage img; // image of asteroid
    boolean isDestroyed; // has been interacted with by an explosion, does not spawn extra explosion (for edge quad tree case)
    boolean willSplit; // if this asteroid will split into multiple
    boolean hasSplit; // has already split
    Queue<PVector> trails;
    float increments;

    /**
     * constructor for the asteroid
     * @param app app to use
     * @param radius radius of the asteroid
     * @param mass mass of the asteroid
     * @param image image of the asteroid
     * @param isSplitting whether the asteroid will split
     */
    public Asteroid(PApplet app, int radius, float mass, PImage image, boolean isSplitting, int numTrails) {
        super(app, new PVector(0, 0), new PVector(0,0), 1/mass, true, radius, radius);
        this.img = image;
        isDestroyed = false;
        this.willSplit = isSplitting;
        hasSplit = false;
        trails = new LinkedList<>();
        for(int i = 0 ; i < numTrails ; i++) {
            trails.offer(new PVector(0,0));
        }
        increments = (float) (1.0/trails.size()) * radius * 2;
    }

    /**
     * reuses the object than go through creation
     * @param x x coord
     * @param y y coord
     * @param targetX the target x coord to target vector
     * @param isSplitting whether the asteroid will split
     */
    void reset(int x, int y, int targetX, boolean isSplitting) {
        isDestroyed = false;
        hasSplit = false;
        position.x = x;
        position.y = y;
        // starting from top of the screen
        if(targetX != -1) {
            velocity.x = targetX- x;
            velocity.y = app.displayHeight-y;
            velocity.normalize();
            velocity.mult(((Shmup) app).asteroidVelocity);
        }
        willSplit = isSplitting;
        for(PVector p : trails) {
            p.x = 0;
            p.y = 0;
        }
    }


    /**
     * draws the asteroid
     */
    void draw() {
        float size = increments;
        app.fill(255);
        for(PVector p : trails) {
            if(p.x != 0 && p.y != 0) {
                app.circle(p.x, p.y, size);
            }
            size += increments;
        }
        app.image(img, position.x, position.y, height*2, height*2);
    }

    /**
     * handles the collision between asteroid and an explosion
     * @param element colliding with
     * @return whether collision was handled
     */
    @Override
    public boolean handleCollision(Collidable element) {
        if(element instanceof Explosion) {
            if(!isDestroyed) {
                ((Shmup) app).addExplosion(position.x, position.y);
                ((Shmup) app).destroyAsteroidFromDisplay(this);
                isDestroyed = true;
                return true;
            }
        }
        return false;
    }
}
