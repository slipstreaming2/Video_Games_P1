import processing.core.PVector;
import processing.core.PApplet;

/**
 * missile fired by player, affected by physics
 */
final class PlayerMissile extends PhysicsObject {
  float moveIncrement; // speed of missile

  /**
   * constructor
   * @param x x coord
   * @param y y coord
   * @param radius radius of missile
   * @param moveIncrement speed of missile
   * @param app app
   * @param mass mass of missile
   */
  PlayerMissile(int x, int y, int radius,
                float moveIncrement, PApplet app, float mass) {
    super(app, new PVector(x, y), new PVector(0,0), 1/mass, true,
            radius, radius);
    this.moveIncrement = moveIncrement;
  }

  /**
   * reuse this object rather than go through object creation
   * @param x x coord
   * @param y y coord
   */
  void reset(int x, int y) {
    position.x = x;
    position.y = y;
    // determine amount of power to put into missile based off of distance between
    // mouse and the ballista when fired
    float dist = app.dist(x, y, app.mouseX, app.mouseY) * moveIncrement;
    velocity.x = x-app.mouseX;
    velocity.y = y-app.mouseY;
    velocity.normalize();
    velocity.mult(-dist);
  }

  /**
   * draws the missile
   */
  void draw() {
    app.fill(255) ;
    app.circle(position.x, position.y, height*2);
  }
}
