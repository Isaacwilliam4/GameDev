package ecs.Systems;

import ecs.Components.Component;
import ecs.Entities.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * The base class for all systems in this ECS environment.
 * Derived classes should provide ComponentTypes to specify the types of Component an Entity
 * must have in order for the system work with it, and update to implement system-specific
 * behavior with matching entities.
 */
public abstract class System {

    protected Map<Long, Entity> entities = new HashMap<>();
    private final Class<? extends Component>[] componentTypes;

    @SafeVarargs
    public System(Class<? extends Component>... types) {
        this.componentTypes = types;
    }

    /**
     * If the entity has all the component types associated with the system, this method
     * returns true, otherwise false.
     */
    protected boolean isInterested(Entity entity) {
        for (var type : componentTypes) {
            if (!entity.contains(type)) {
                return false;
            }
        }

        return true;
    }

    /**
     * If the system is interested in the entity, it is added to the tracking collection
     */
    public boolean add(Entity entity) {
        boolean interested = isInterested(entity);
        if (interested) {
            entities.put(entity.getId(), entity);
        }
        return interested;
    }

    /**
     * Removes the entity from the tracking collection.  If the entity was actually in
     * the system true is returned, false otherwise.
     */
    public boolean remove(long id) {
        return entities.remove(id) != null;
    }

    /**
     * Derived systems must override this method to perform update logic specific to that system.
     */
    public abstract void update(double elapsedTime);
}
