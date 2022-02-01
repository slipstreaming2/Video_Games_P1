import processing.core.PApplet;
import processing.core.PVector;

public class City {
    PApplet app;
    int height;
    int width;
    PVector position;
    boolean isDestroyed;

    public City(PApplet app, int height, int width, int x, int y) {
        this.app = app;
        this.height = height;
        this.width = width;
        position = new PVector(x, y);
        isDestroyed = false;
    }

    void draw() {
        if(!isDestroyed) app.rect(position.x, position.y, width, height);
    }

    void rebuildCity() {
        isDestroyed = false;
    }
}
