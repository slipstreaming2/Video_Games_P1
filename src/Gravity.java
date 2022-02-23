import processing.core.PVector;

/**
 * A force generator that applies a gravitational force.
 * One instance can be used for multiple particles.
 * With thanks to the lectures
 */
public final class Gravity extends ForceGenerator {

    // Holds the acceleration due to gravity
    private PVector gravity ;

    /**
     * Constructs the generator with the given acceleration
     * @param gravity gravity given
     */
    Gravity(PVector gravity) {
        this.gravity = gravity ;
    }



    /**
     * This assumes the particle is small, with constant mass,
     * and gravity is being exerted on it by something relatively
     * massive.
     * @param particle particle to update force upon
     */
    @Override
    void updateForce(PhysicsObject particle) {
        //should check for infinite mass
        //apply mass-scaled force to the particle
        PVector resultingForce = gravity.copy();
        resultingForce.mult(particle.getMass());
        particle.addForce(resultingForce);
    }
}