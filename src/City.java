import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;
import ddf.minim.AudioPlayer;

/**
 * class to represent a city, it is a gameobject that is collidable
 */
public class City extends GameObject implements Collidable {
    boolean isDestroyed;
    PImage img;
    AudioPlayer destroyedSound;

    /**
     * constructor of city
     * @param app app
     * @param height height
     * @param width width
     * @param x x coord
     * @param y y coord
     * @param image image of city
     * @param sound sound to play if city destroyed
     */
    public City(PApplet app, int height, int width, int x, int y, PImage image, AudioPlayer sound) {
        super(app, new PVector(x, y), false, height, width);
        isDestroyed = false;
        this.img = image;
        this.destroyedSound = sound;
    }

    /**
     * draws the city only if it is not destroyed
     */
    void draw() {
        if(!isDestroyed){
            app.image(img, position.x, position.y, (float) width, (float) height);
        }

    }

    /**
     * rebuilds the city, adds back to drawing
     */
    void rebuildCity() {
        isDestroyed = false;
        ((Shmup) app).addToDraw(this);
    }

    /**
     * destroys the city, adds the destroyed sound to play
     */
    void destroyedCity() {
        isDestroyed = true;
        ((Shmup) app).addSound(destroyedSound);
        ((Shmup) app).removeFromDraw(this);
    }

    /**
     * handles the collision between city and asteroid
     * @param element element colliding with
     * @return whether collision was handled
     */
    @Override
    public boolean handleCollision(Collidable element) {
        if(element instanceof Asteroid) {
            if(element instanceof SmartBomb) { // if smartbomb, check actual smartbomb radius
                if(this.checkCollision((GameObject) element, true)) {
                    destroyedCity();
                    return true;
                }
            } else{
                destroyedCity();
                return true;
            }
        }
        return false;
    }
}
