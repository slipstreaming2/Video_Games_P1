import processing.core.PApplet;
import processing.core.PVector;

public class Explosion {
    PApplet app;
    PVector position;
    int radius;
    int expansionRatio;
    int sizeLimit;


    public Explosion(PApplet app, int x, int y, int radius, int expansionRatio, int sizeLimit) {
        this.app = app;
        position = new PVector(x, y) ;
        this.radius = -expansionRatio ;
        this.expansionRatio = expansionRatio ;
        this.app = app;
        this.sizeLimit = sizeLimit;
    }

    void draw() {
        expand();
        app.fill(200);
        app.circle(position.x, position.y, radius*2);
    }

    // reuse this object rather than go through object creation
    void reset(float x, float y) {
        position.x = x ;
        position.y = y ;
        radius = 0;
    }

    void expand() {
        radius += expansionRatio;
    }

    boolean fullyExploded() {
        return radius > sizeLimit;
    }
}
