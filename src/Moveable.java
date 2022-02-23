import processing.core.PApplet;
import processing.core.PVector;

/**
 * simple class to determine if a gameobject moves at all
 */
public abstract class Moveable extends GameObject {

    /**
     * constructor
     * @param app app
     * @param position position of obj
     * @param isCircle whether object is circular
     * @param height height
     * @param width width
     */
    public Moveable(PApplet app, PVector position, boolean isCircle, float height, float width) {
        super(app, position, isCircle, height, width);
    }

    /**
     * determines if the object is not in bounds
     * @return true if not in bounds
     */
    public boolean notInBounds() {
        return position.x > app.displayWidth + width/2 ||
                    position.x < -width/2 ||
                    position.y > app.displayHeight + height/2 ||
                    position.y < -height/2;
    }

    /**
     * all movable objects must move
     */
    abstract void move();

}
