import processing.core.PApplet;
import processing.core.PVector;

import java.util.Objects;

/**
 * a physics object is one that is moveable
 * class with thanks to lectures
 */
public abstract class PhysicsObject extends Moveable {
    // Vectors to hold vel
    public PVector velocity ;

    // Vector to accumulate forces prior to integration
    private PVector forceAccumulator ;

    // Store inverse mass to allow simulation of infinite mass
    private float invMass;

    /**
     * gets the velocity of obj
     * @return velocity
     */
    public PVector getVelocity() {
        return velocity;
    }

    /**
     * constructor
     * @param app app
     * @param position pos
     * @param velocity velocity of obj
     * @param invM the inverse mass
     * @param isCircle whether circular
     * @param height height
     * @param width width
     */
    public PhysicsObject(PApplet app, PVector position, PVector velocity, float invM,
                         boolean isCircle, float height, float width) {
        super(app, position, isCircle, height, width);
        forceAccumulator = new PVector(0, 0) ;
        this.invMass = invM;
        this.velocity = velocity;
    }

    /**
     * returns mass if necessary
     * @return mass of obj
     */
    public float getMass() {return 1/invMass ;}

    /**
     * add force to accumulator
     * @param force force to add to accumulator
     */
    void addForce(PVector force) {
        forceAccumulator.add(force);
    }

    /**
     * udpates position and velocity based on forces
     */
    void move() {
        // If infinite mass, we don't integrate
        if (invMass <= 0f) return;

        if(this instanceof Asteroid) {
            ((Asteroid) this).trails.poll();
            PVector p = position.copy();
            ((Asteroid) this).trails.offer(p);
        }

        // update position
        position.add(velocity);

        // F = ma
        // a = F * 1/m
        PVector resultingAcceleration = forceAccumulator.copy();
        resultingAcceleration.mult(invMass);

        // update velocity
        velocity.add(resultingAcceleration);

        // Clear accumulator
        forceAccumulator.x = 0;
        forceAccumulator.y = 0;
    }

}
