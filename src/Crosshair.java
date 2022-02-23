import processing.core.PApplet;

/**
 * a simple class to display a crosshair
 */
public class Crosshair extends GameObject {
    int offset_height;
    int offset_width;

    /**
     * constructor
     * @param app app
     * @param width width
     * @param height height
     * @param offset_height how much to offset the crosshair height
     * @param offset_width how much to offset width
     */
    public Crosshair(PApplet app, int width, int height, int offset_height, int offset_width)  {
        super(app, null, false, height, width);
        this.offset_height = offset_height;
        this.offset_width = offset_width;
    }

    /**
     * draws the crosshair
     */
    void draw() {
        app.fill(255);
        app.rect(app.mouseX - width/2, app.mouseY - height/2 - offset_height, width, height);
        app.rect(app.mouseX - width/2, app.mouseY - height/2 + offset_height,
                width, height);
        app.rect(app.mouseX - width/2 - offset_width, app.mouseY - height/2,
                width, height);
        app.rect(app.mouseX - width/2 + offset_width, app.mouseY - height/2, width, height);
    }

}
