/**
 * interface to show an object is collidable
 */
public interface Collidable {
    /**
     * checks if there is a collision between this object and the element passed in
     * @param element possibly colliding with element
     * @param checkSmartBomb if it is a smartbomb, use actual radius
     * @return true if colliding, false otherwise
     */
    default boolean checkCollision(GameObject element, boolean checkSmartBomb) {
        if(((GameObject) this).isCircular) {
            if(!element.isCircular) {
                return circleRectCollision((GameObject) this, element, checkSmartBomb);
            } else {
                return circleCircleCollision((GameObject) this, element, checkSmartBomb);
            }
        } else {
            if(element.isCircular) {
                return circleRectCollision(element, (GameObject) this, checkSmartBomb);
            }
        }
        return false;
    }

    /**
     * checks collision between a circle and rectangle collision
     * @param oc gameobject
     * @param r gameobject
     * @param checkSmartBomb whether to use smartbomb real radius
     * @return true if collision detected
     */
    default boolean circleRectCollision(GameObject oc, GameObject r, boolean checkSmartBomb) {
        float radius;
        if(checkSmartBomb && oc instanceof SmartBomb) radius = ((SmartBomb) oc).actualRadius*2;
        else radius = oc.height;
        // with thanks to:
        // https://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection
        double x = Math.abs(oc.position.x - r.position.x);
        double y = Math.abs(oc.position.y - r.position.y);

        // checks collision not on corners
        if (x > (r.width/2 + radius)) { return false; }
        if (y > (r.height/2 + radius)) { return false; }

        if (x <= (r.width/2)) { return true; }
        if (y <= (r.height/2)) { return true; }

        // checks corner collision
        double cornerDistance_sq = Math.pow(x - r.width/2, 2) +
                Math.pow(y - r.height/2, 2);

        return (cornerDistance_sq <= Math.pow(radius, 2));
    }

    /**
     * checks circle circle collision
     * @param oc gameobject
     * @param c gameobject
     * @param checkSmartBomb whether to use real smartbomb radius
     * @return ture if colliding
     */
    default boolean circleCircleCollision(GameObject oc, GameObject c, boolean checkSmartBomb) {
        float radius1, radius2;
        if(checkSmartBomb && oc instanceof SmartBomb) {
            radius1 = ((SmartBomb) oc).actualRadius;
        } else {
            radius1 = oc.height; // height is just radius
        }
        if(checkSmartBomb && c instanceof SmartBomb) {
            radius2 = ((SmartBomb) c).actualRadius;
        } else {
            radius2 = c.height;
        }
        return c.app.dist(c.position.x, c.position.y, oc.position.x, oc.position.y)
                <= radius1 + radius2;
    }

    /**
     * collidable objects must handle collisions individually
     * @param element element to handle collision against
     * @return true if collision was handled
     */
    boolean handleCollision(Collidable element);
}


