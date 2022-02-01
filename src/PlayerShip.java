import processing.core.PApplet;
import processing.core.PVector;

final class PlayerShip {
  // The player's ship is represented as a handsome rectangle
  // the position field indicates the top-left of that rectangle.
  PVector position ;
  int playerWidth, playerHeight ;
  int moveIncrement ;
  PApplet app;
  
  PlayerShip(int x, int y, int playerWidth, int playerHeight, int moveIncrement, PApplet app) {
    position = new PVector(x, y) ;
    this.playerWidth = playerWidth ;
    this.playerHeight = playerHeight ;
    this.moveIncrement = moveIncrement ;
    this.app = app;
  }
  
  // getters
  int getX() {return (int)position.x ;}
  int getY() {return (int)position.y ;}
  
  // The player's ship is displayed as a rectangle
  void draw() {
    app.fill(255) ;
    app.rect(position.x, position.y, playerWidth, playerHeight) ;
  }
  
  // Handle movement - do you want to support up/down movement too?
  void moveLeft() {
    position.x -= moveIncrement ;
    if (position.x < 0) position.x = 0 ;
  }
  void moveRight() {
    position.x += moveIncrement ;
    if (position.x > app.displayWidth-playerWidth)
      position.x = app.displayWidth-playerWidth ;
  }  
}
