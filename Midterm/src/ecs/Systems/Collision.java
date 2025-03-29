package ecs.Systems;

import ecs.Components.Movable;
import ecs.Components.Position;
import ecs.Entities.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Collision extends System {

    public interface IFoodConsumed {
        void invoke(Entity entity);
    }

    private final IFoodConsumed foodConsumed;

    public Collision(IFoodConsumed foodConsumed) {
        super(ecs.Components.Position.class);

        this.foodConsumed = foodConsumed;
    }

    /**
     * Check to see if any movable components collide with any other
     * collision components.
     * <p>
     * Step 1: find all movable components first
     * Step 2: Test the movable components for collision with other (but not self) collision components
     */
    @Override
    public void update(double elapsedTime) {
        var movable = findMovable(entities);

        for (var entity : entities.values()) {
            for (var entityMovable : movable) {
                if (collides(entity, entityMovable)) {
                    entityMovable.get(Position.class).undoMove();
                }
            }
        }
    }

    /**
     * Public method that allows an entity with a single cell position
     * to be tested for collision with anything else in the game.
     */
    public boolean collidesWithAny(Entity proposed) {
        var aPosition = proposed.get(ecs.Components.Position.class);

        for (var entity : entities.values()) {
            if (entity.contains(ecs.Components.Collision.class) && entity.contains(ecs.Components.Position.class)) {
                var ePosition = entity.get(ecs.Components.Position.class);

                if (aPosition.getX() == ePosition.getX() && aPosition.getY() == ePosition.getY()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns a collection of all the movable entities.
     */
    private List<Entity> findMovable(Map<Long, Entity> entities) {
        var movable = new ArrayList<Entity>();

        for (var entity : entities.values()) {
            if (entity.contains(ecs.Components.Movable.class) && entity.contains(ecs.Components.Position.class)) {
                movable.add(entity);
            }
        }

        return movable;
    }

    /**
     * We know that only the snake is moving and that we only need
     * to check its head for collision with other entities.  Therefore,
     * don't need to look at all the segments in the position, with the
     * exception of the movable itself...a movable can collide with itself.
     */
    private boolean collides(Entity a, Entity b) {

        if (a.contains(ecs.Components.Collision.class) && b.contains(ecs.Components.Collision.class) && !a.equals(b)) {
            var aPosition = a.get(ecs.Components.Position.class);
            var bPosition = b.get(ecs.Components.Position.class);

            return aPosition.getX() == bPosition.getX() && aPosition.getY() == bPosition.getY();
        }
        return false;
    }

}
