import processing.core.PApplet;
import processing.core.PVector;

/**
 * simplest object within a game, needs only position, whether it is a circle,
 * and the height and width. Note if it is a circle, height = width
 */
public abstract class GameObject {
    PVector position;
    PApplet app;
    boolean isCircular;
    float height, width;

    /**
     * constructor for a game object
     * @param app app to use for drawing etc
     * @param position objects position
     * @param isCircle whether the object is circular
     * @param height height
     * @param width width
     */
    public GameObject(PApplet app, PVector position, boolean isCircle, float height, float width) {
        this.app = app;
        this.position = position;
        this.isCircular = isCircle;
        this.height = height;
        this.width = width;
    }

    /**
     * all game objects must draw
     */
    abstract void draw();
}
