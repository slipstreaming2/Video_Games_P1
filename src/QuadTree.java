import processing.core.PApplet;

import java.awt.*;
import java.util.*;

/**
 * Class implementing the Quad Tree for collision detection
 * With majority of implementation thanks to:
 * https://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
 */
public class QuadTree {
    private int MAX_OBJECTS = 3; // max number of objects before a split
    private int MAX_LEVELS = 3; // max depth of splits of nodes

    private int level; // current level
    private Stack<Collidable> objects; // objects at this level of nodes
    private Rectangle bounds; // bounds of the current quad
    private QuadTree[] nodes; // child nodes
    private PApplet app;

    /**
     * Constructor, pBounds measured from top left corner
     * @param pLevel current level of node
     * @param pBounds bounds of this node
     * @param app PApplet app
     */
    public QuadTree(int pLevel, Rectangle pBounds, PApplet app) {
        level = pLevel;
        objects = new Stack<>();
        bounds = pBounds;
        nodes = new QuadTree[4];
        this.app = app;
    }

    /**
     * clears the quadtree recursively
     */
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    /**
     * splits the nodes into four subnodes
     */
    private void split() {
        int subWidth = (int)(bounds.getWidth() / 2);
        int subHeight = (int)(bounds.getHeight() / 2);
        int x = (int)bounds.getX();
        int y = (int)bounds.getY();

        nodes[0] = new QuadTree(level+1, new Rectangle(x + subWidth, y, subWidth, subHeight), app);
        nodes[1] = new QuadTree(level+1, new Rectangle(x, y, subWidth, subHeight), app);
        nodes[2] = new QuadTree(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight), app);
        nodes[3] = new QuadTree(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight), app);
    }

    /**
     * Determines which node the element belongs to
     * Quadrants assigned as:
     * 1 | 0
     * -----
     * 2 | 3
     * with extra indexes added on the overlapping quadrant lines as
     *     |
     *     5
     *     |
     * --6-8-4--
     *     |
     *     7
     *     |
     * @param elem element to insert into quadtree
     * @return integer of the index of the element in the quadtree
     */
    private int getIndex(GameObject elem) {
        int index = 8; // overlaps all quadrants
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        double height= elem.height;
        double width;
        if(!elem.isCircular) {
            width = elem.width;
        } else {
            // if element is circular, width = height
            // want diameter so multiply by 2
            width = height*2;
            height *= 2;
        }
        // positions given as center, measuring from top left corner easier
        double y = elem.position.y - height/2;
        double x = elem.position.x - width/2;
        // Object can completely fit within the top quadrants
        boolean topQuadrant = (y < horizontalMidpoint && y + height < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (y > horizontalMidpoint);


        // Object can completely fit within the left quadrants
        if (x < verticalMidpoint && x + width < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            }
            else if (bottomQuadrant) {
                index = 2;
            }
            // cannot fit into top or bottom quadrant
            // but fits into right quadrants
            else {
                index = 4;
            }
        }
        // Object can completely fit within the right quadrants
        else if (x > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            }
            else if (bottomQuadrant) {
                index = 3;
            }
            else{
                // cannot fit into top or bottom quadrant
                // but fits into left quadrants
                index = 6;
            }
        }
        else if(topQuadrant) {
            index = 5; // fits top quadrant
        } else if(bottomQuadrant) {
            index = 7; // fits bottom quadrant
        }
        return index;
    }

    /**
     * recursively draws outline of quadtree for visualisation
     */
    void draw() {
        double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
        double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

        if(nodes[0] != null) {
            app.stroke(255);
            app.line((float) bounds.getX(), (float) horizontalMidpoint,
                    (float) (bounds.getX()+bounds.getWidth()), (float) horizontalMidpoint);
            app.line((float) verticalMidpoint, (float) bounds.getY(),
                    (float) verticalMidpoint, (float) (bounds.getY()+bounds.getHeight()));
            for (QuadTree node : nodes) {
                node.draw();
            }
        }
    }

    /**
     * inserts in the element into the quadtree
     * if the max number of objects per quad exceeded, split into further nodes
     * @param elem element to insert into the quadtree
     */
    public void insert(Collidable elem) {
        // add node into sub-tree
        if (nodes[0] != null) {
            int index = getIndex((GameObject) elem);

            if (index < 4) {
                nodes[index].insert(elem);
                return; // exit out, added to sub-tree
            }
//           else {
//               switch(index) {
//                   case 4:
//                       nodes[0].insert(elem);
//                       nodes[3].insert(elem);
//                       break;
//                   case 5:
//                       nodes[0].insert(elem);
//                       nodes[1].insert(elem);
//                       break;
//                   case 6:
//                       nodes[1].insert(elem);
//                       nodes[2].insert(elem);
//                       break;
//                   case 7:
//                       nodes[2].insert(elem);
//                       nodes[3].insert(elem);
//                       break;
//                   case 8:
//                       for (QuadTree node : nodes) {
//                           node.insert(elem);
//                       }
//                       break;
//               }
//           }
            // legacy code
             else {
                 for (QuadTree node : nodes) {
                     node.insert(elem);
                 }
             }
            return;
        }

        objects.push(elem);

        // exceeded max objects and the depth has yet to exceed max levels
        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            // have not split nodes yet
            if (nodes[0] == null) {
                split();
            }

            // place objects into respective sub-trees
            while (!objects.isEmpty()) {
                insert(objects.pop());
            }
        }
    }

    /**
     * checks the collision between the list of collidable objects
     * @param collide elements to check collision against
     */
    public void collisionCheck(Stack<Collidable> collide) {
        for(int i = 0 ; i < collide.size() ; i++) {
            Collidable a = collide.get(i);
            for (int j = i + 1; j < collide.size(); j++) {
                Collidable b = collide.get(j);
                // objects are colliding
                if (a.checkCollision((GameObject) b, false)) {
                    if (!a.handleCollision(b)) { // object a did not handle collision
                        b.handleCollision(a); // b object handles the collision
                    }
                }
            }
        }
    }

    /**
     * recursively checks collision within the tree
     */
    public void checkCollisions() {
        // have further depth to check
        if(nodes[0] != null) {
            for(int i = 0 ; i < nodes.length ; i++) {
                nodes[i].checkCollisions();
            }
        } else { // leaf node
            collisionCheck(objects);
        }
    }
}
