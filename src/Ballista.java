import ddf.minim.AudioPlayer;
import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;
import java.lang.Math;
import java.util.Stack;

import static processing.core.PConstants.HALF_PI;

/**
 * Ballista class, it is only a game object, but also collidable
 */
public class Ballista extends GameObject implements Collidable {
    float rotation; // current rotation of ballista
    Stack<PlayerMissile> missiles; // current available missiles
    Stack<PlayerMissile> usedMissiles; // used up missiles
    int textSize; // text size of remaining missiles
    int textOffset; // offset of remaining missiles
    int platformOffset; // platform indicator of which ballista chosen offset
    boolean isDestroyed; // whether ballista has been destroyed
    boolean playedLowAmmoSound; // whether ammo sound has been played
    boolean isSelected; // selected by the player
    PImage img;
    AudioPlayer lowAmmoSound;
    AudioPlayer shotSound;
    AudioPlayer destroyedSound;

    /**
     * ballista class constructor
     * @param app app to reference
     * @param x x coord
     * @param y y coord
     * @param height height
     * @param width width
     * @param missiles missiles to use
     * @param textSize text size
     * @param textOffset text offset
     * @param img image of ballista
     * @param lowAmmoSound when ammo is low play this
     * @param shotSound when shooting play this
     * @param destroyedSound when destroyed play this
     * @param platformOffset platform offset
     */
    public Ballista(PApplet app, int x, int y, int height, int width,
                    Stack<PlayerMissile> missiles, int textSize, int textOffset, PImage img,
                    AudioPlayer lowAmmoSound, AudioPlayer shotSound, AudioPlayer destroyedSound,
                    int platformOffset) {
        super(app, new PVector(x, y), false,height, width);
        rotation = HALF_PI;
        this.missiles = missiles;
        usedMissiles = new Stack<>();
        this.textOffset = textOffset;
        this.textSize = textSize;
        this.img = img;
        this.lowAmmoSound = lowAmmoSound;
        this.shotSound = shotSound;
        this.destroyedSound = destroyedSound;
        this.platformOffset = platformOffset;
        isDestroyed = false;
        playedLowAmmoSound = false;
        isSelected = false;
    }

    /**
     * draws the ballista with rotation
     */
    void draw() {
        if (isDestroyed) return; // destroyed, do not draw anything
        app.textSize(textSize);
        app.pushMatrix();
        app.translate(position.x, position.y);
        app.rotate(rotation - HALF_PI); // rotate so ballista points upright at mouse
        app.image(img, 0, 0, width, height);
        app.popMatrix();

        if (isSelected) app.fill(255, 255, 0); // selected by player, highlight platform
        else app.fill(0, 0, 125);
        app.rect(position.x, position.y + platformOffset, width / 2, height / 2);
        if (missiles.size() < 4) { // low on ammo, play sound and change ammo text color to red
            if (!playedLowAmmoSound) {
                ((Shmup) app).addSound(lowAmmoSound);
                playedLowAmmoSound = true;
            }
            app.fill(255, 0, 0);
        } else {
            app.fill(159, 123, 255); // purple
        }
        app.text(missiles.size(), position.x, position.y + textOffset);
    }

    /**
     * changes rotation of the ballista
     */
    void changeRotation() {
        double y = position.y-app.mouseY,
                x = position.x-app.mouseX;
        rotation = (float) Math.atan2(y,x);
    }

    /**
     * fires a missile
     * @return a missile that has been fired
     */
    PlayerMissile fire() {
        if(missiles.isEmpty() || isDestroyed) return null; // cannot fire
        PlayerMissile missile = missiles.pop();
        missile.reset((int)position.x, (int)position.y);
        usedMissiles.push(missile); // save used missiles to reuse
        ((Shmup) app).addSound(shotSound);
        return missile;
    }

    /**
     * resets the ballista for another round
     */
    void resetBallista() {
        playedLowAmmoSound = false;
        isDestroyed = false;
        while(!usedMissiles.isEmpty()) { // put all missiles back
            missiles.push(usedMissiles.pop());
        }
        ((Shmup) app).addToDraw(this); // add back to draw
    }

    /**
     * ballista has been destroyed, remove from draw and play destroyed sound
     */
    void destroyBallista() {
        isDestroyed = true;
        ((Shmup) app).addSound(destroyedSound);
        ((Shmup) app).removeFromDraw(this);
    }

    /**
     * handles collision of asteroid
     * @param element what we are colliding with
     * @return whether collision was handled
     */
    @Override
    public boolean handleCollision(Collidable element) {
        if(element instanceof Asteroid) {
            if(element instanceof SmartBomb) { // if it is a smartbomb, check if we really did collide
                if(this.checkCollision((GameObject) element, true)) {
                    destroyBallista();
                    return true;
                }
            } else {
                destroyBallista();
                return true;
            }
        }
        return false;
    }
}
