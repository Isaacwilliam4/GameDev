package ecs.Systems;

import ecs.Components.Movable;
import ecs.Components.Position;
import ecs.Entities.Entity;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Collision extends System {

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
                    var collsionComponent = entity.get(ecs.Components.Collision.class);
                    collsionComponent.isCollided = true;
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
            var aAppearance = a.get(ecs.Components.Appearance.class);
            var bAppearance = b.get(ecs.Components.Appearance.class);

            if (isCollision(aPosition.position, bPosition.position, aAppearance.width, aAppearance.height, bAppearance.width, bAppearance.height)) {
                return true;
            }
        }
        return false;
    }


    private boolean isCollision(Vector2f aPos, Vector2f bPos, float aWidth, float aHeight, float bWidth, float bHeight) {
        float aLeft = aPos.x - (aWidth);
        float aRight = aPos.x + (aWidth);
        float aTop = aPos.y - (aHeight);
        float aBottom = aPos.y + (aHeight);

        float bLeft = bPos.x - (bWidth);
        float bRight = bPos.x + (bWidth);
        float bTop = bPos.y - (bHeight);
        float bBottom = bPos.y + (bHeight);

        return (aLeft < bRight && aRight > bLeft && aTop < bBottom && aBottom > bTop);
    }

}
