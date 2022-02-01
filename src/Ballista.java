import processing.core.PApplet;
import processing.core.PVector;
import java.lang.Math;
import java.util.Stack;

import static processing.core.PConstants.HALF_PI;
import static processing.core.PConstants.PI;

public class Ballista {
    PApplet app;
    PVector position;
    int height;
    int width;
    float rotation;
    Stack<PlayerMissile> missiles;
    Stack<PlayerMissile> usedMissiles;
    int textSize;
    int textOffset;
    boolean isDestroyed;

    public Ballista(PApplet app, int x, int y, int height, int width,
                    Stack<PlayerMissile> missiles, int textSize, int textOffset) {
        this.app = app;
        position = new PVector(x, y);
        this.height = height;
        this.width = width;
        rotation = 0;
        this.missiles = missiles;
        usedMissiles = new Stack<>();
        this.textOffset = textOffset;
        this.textSize = textSize;
        isDestroyed = false;
    }

//    int getX() {return (int)position.x;}
//    int getY() {return (int)position.y+height/2;}
//    float getRotation() {return rotation;}

    void draw() {
        if(!isDestroyed) {
            app.textSize(textSize);
            app.pushMatrix();
//        changeRotation();
            app.translate(position.x, position.y);
            app.rotate(rotation);
//        app.translate(-app.displayWidth/2, -app.displayHeight/2);
            app.fill(255);
            app.rect(0, 0, width, height);
            app.popMatrix();
            app.fill(0, 408, 612); // blue
            app.text(missiles.size(), position.x, position.y + textOffset);
        }
    }

    void changeRotation() {
        double y = position.y-app.mouseY,
                x = position.x-app.mouseX;
        rotation = (float) Math.atan2(y,x);
//        System.out.println(rotation);
    }

    PlayerMissile fire() {
        if(missiles.isEmpty() || isDestroyed) return null;
        PlayerMissile missile = missiles.pop();
        missile.reset((int)position.x, (int)position.y, rotation);
        usedMissiles.push(missile);
        return missile;
    }

    void resetBallista() {
        isDestroyed = false;
        while(!usedMissiles.isEmpty()) {
            missiles.push(usedMissiles.pop());
        }
    }

}
