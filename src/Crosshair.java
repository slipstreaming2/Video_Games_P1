import processing.core.PApplet;

public class Crosshair {

    PApplet app;
    int width;
    int height;
    int offset_height;
    int offset_width;


    public Crosshair(PApplet app, int width, int height, int offset_height, int offset_width) {
        this.app = app;
        this.width = width;
        this.height = height;
        this.offset_height = offset_height;
        this.offset_width = offset_width;
    }

    void draw() {
        app.rect(app.mouseX - width/2, app.mouseY - height/2 - offset_height, width, height);
        app.rect(app.mouseX - width/2, app.mouseY - height/2 + offset_height,
                width, height);
        app.rect(app.mouseX - width/2 - offset_width, app.mouseY - height/2,
                width, height);
        app.rect(app.mouseX - width/2 + offset_width, app.mouseY - height/2, width, height);
    }

}
