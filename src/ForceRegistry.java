import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator ;
import java.util.Map;

/**
 * Holds all the force generators and the particles they apply to
 * With thanks to the lectures
 */
class ForceRegistry {
    HashMap<PhysicsObject, ArrayList<ForceGenerator>> registrations = new HashMap<>();

    /**
     * Register the given force to apply to the given particle
     */
    void add(PhysicsObject p, ForceGenerator fg) {
        if(registrations.containsKey(p)) {
            registrations.get(p).add(fg);
        } else {
            ArrayList<ForceGenerator> f = new ArrayList<>();
            f.add(fg);
            registrations.put(p, f);
        }
    }

    /**
     * Remove the given registered pair from the registry. If the
     * pair is not registered, this method will have no effect.
     */
    void remove(PhysicsObject p) {
        registrations.remove(p);
    }

    /**
     * Clear all registrations from the registry
     */
    void clear() {
        registrations.clear() ;
    }

    /**
     * Calls all force generators to update the forces of their
     *  corresponding particles.
     */
    void updateForces() {
        for(Map.Entry<PhysicsObject, ArrayList<ForceGenerator>> entry : registrations.entrySet()) {
            for(ForceGenerator fg : entry.getValue()) {
                fg.updateForce(entry.getKey());
            }
        }
    }
}