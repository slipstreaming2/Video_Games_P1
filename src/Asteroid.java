import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Queue;

public class Asteroid {
    PApplet app;
    int radius;
    int velocity;
    PVector position;
    PVector vector;

    public Asteroid(PApplet app, int radius, int velocity) {
        this.app = app;
        this.radius = radius;
        this.velocity = velocity;
        position = new PVector(0, 0);
        vector = new PVector(1,1);
    }

    // reuse this object rather than go through object creation
    void reset(int x, int y, int targetX) {
        position.x = x;
        position.y = y;
        vector.x = x-targetX+radius/2;
        vector.y = y-app.displayHeight+radius/2;
        vector.normalize();
        vector.mult(velocity);
    }

    void draw() {
        app.fill(255);
        app.circle(position.x, position.y, radius*2);
    }

    boolean move() {
        position.sub(vector);
        return (position.y) >= 0 && position.x >= 0;
    }

    boolean checkExplosionCollision(Queue<Explosion> explosionsDisplayed) {
        for(Explosion e : explosionsDisplayed) {
            if(app.dist(e.position.x, e.position.y, position.x, position.y)
                    <= e.radius + radius) {
                return true;
            }
        }
        return false;
    }

    int checkCityCollision(ArrayList<City> cities) {
        if(position.y+radius < cities.get(0).position.y+cities.get(0).height/2) return -1;
        for(int i = 0 ; i < cities.size() ; i++) {
            City c = cities.get(i);
            if(c.position.x - c.width/2 > position.x + radius || c.position.x + c.width/2 < position.x - radius) continue;
            if(c.position.y + c.height/2 > radius+position.y) continue;
            return i;
        }
        return -1;
    }

    int checkBallistaCollision(ArrayList<Ballista> ballistas) {
        if(position.y+radius < ballistas.get(0).position.y) return -1;
        for(int i = 0 ; i < ballistas.size() ; i++) {
            Ballista b = ballistas.get(i);
            if(b.position.x - b.width/2 > position.x + radius || b.position.x + b.width/2 < position.x - radius) continue;
            if(b.position.y + b.height/2 > radius+position.y) continue;
            return i;
        }
        return -1;
    }



}
