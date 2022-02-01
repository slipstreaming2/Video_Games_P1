import processing.core.PVector;
import processing.core.PApplet;

final class PlayerMissile {
  // The missile is represented by another handsome rectangle
  // the position field indicates the top-left of that rectangle.  
  PVector position ;
  PVector velocity;
  int missileWidth, missileHeight ;
  float rotation;
  int moveIncrement ;
  PApplet app;
  
  PlayerMissile(int x, int y, int missileWidth, int missileHeight, int moveIncrement, PApplet app) {
    position = new PVector(x, y) ;
    this.missileWidth = missileWidth ;
    this.missileHeight = missileHeight ;
    this.moveIncrement = moveIncrement ;
    this.app = app;
    velocity = new PVector(1,1);
  }
  
  // reuse this object rather than go through object creation
  void reset(int x, int y, float rotation) {
    this.rotation = rotation;
    position.x = x ;
    position.y = y ;
    velocity.x = x-app.mouseX;
    velocity.y = y-app.mouseY;
    velocity.normalize();
    velocity.mult(moveIncrement);
  }
  
  // The missile is displayed as a rectangle
  void draw() {
//    app.pushMatrix();
//    app.translate(position.x, position.y);
//    app.rotate(rotation);
    app.fill(200) ;
    app.circle(position.x, position.y, missileHeight);
//    app.rect(-missileWidth, -missileHeight, missileWidth, missileHeight) ;
//    app.popMatrix();
  }
  
  // handle movement. Returns true if not out of play area

  boolean move() {
    position.sub(velocity);
    return (position.y) >= 0 && position.x >= 0 ;
  }  
}
