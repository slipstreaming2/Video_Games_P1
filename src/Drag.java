import processing.core.PVector;

/**
 * drag class from lectures, extends forcegen as a force
 */
public class Drag extends ForceGenerator {
    private float k1, k2; // k1 and k2 are drag constants given for an object

    /**
     * constructor
     * @param k1 low speed drag coefficient
     * @param k2 high speed drag coefficient
     */
    Drag(float k1, float k2) {
        this.k1 = k1;
        this.k2 = k2;
    }

    /**
     * Applies the drag force to the given particle
     * @param particle particle to apply drag to
     */
    @Override
    public void updateForce(PhysicsObject particle) {
        PVector force = particle.getVelocity().copy() ;

        //Calculate the total drag coefficient
        float dragCoeff = force.mag() ;
        dragCoeff = k1 * dragCoeff + k2 * dragCoeff * dragCoeff ;

        //Calculate the final force and apply it
        force.normalize() ;
        force.mult(-dragCoeff) ;
        particle.addForce(force) ;
    }
}
