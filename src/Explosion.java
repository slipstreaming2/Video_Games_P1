import processing.core.PApplet;
import processing.core.PVector;
import ddf.minim.AudioPlayer;

/**
 * explosion class, it is collidable with other objects
 */
public class Explosion extends GameObject implements Collidable {
    int expansionRatio;
    int sizeLimit;
    int radius;
    AudioPlayer sound;

    /**
     * constructor
     * @param app app
     * @param x x coord
     * @param y y coord
     * @param radius initial radius of explosion
     * @param expansionRatio ratio to expand radius by
     * @param sizeLimit max size of explosion
     * @param sound sound of explosion to play
     */
    public Explosion(PApplet app, int x, int y, int radius, int expansionRatio, int sizeLimit, AudioPlayer sound) {
        super(app, new PVector(x, y), true, radius, radius);
        this.expansionRatio = expansionRatio ;
        this.sizeLimit = sizeLimit;
        this.radius = radius;
        this.sound = sound;
    }

    /**
     * draws the explosion
     */
    void draw() {
        app.fill(125,0,0);
        app.circle(position.x, position.y, height*2);
    }

    /**
     * reuse this object rather than go through object creation
     * @param x x coord
     * @param y y coord
     */
    void reset(float x, float y) {
        ((Shmup) app).addSound(sound); // adds explosion sound
        position.x = x ;
        position.y = y ;
        height = radius;
        width = radius;
    }

    /**
     * expands the explosion
     * expands both height and width to maintain equality
     */
    void expand() {
        height += expansionRatio;
        width += expansionRatio;
    }

    /**
     * whether fully exploded
     * @return true if fully exploded
     */
    boolean fullyExploded() {
        return height > sizeLimit;
    }

    /**
     * handles collision
     * @param element element to handle collision against
     * @return false given all collision handling done in other classes
     */
    @Override
    public boolean handleCollision(Collidable element) {
        return false;
    }
}
