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

        for (var entity : entities.values()) {
            for (var enitity2 : entities.values()) {
                if (!entity.equals(enitity2)) {
                    if (collides(entity, enitity2)) {
                        var collsionComponent = entity.get(ecs.Components.Collision.class);
                        collsionComponent.isCollided = true;
                    }
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
        float aHalfWidth = aWidth / 2;
        float aHalfHeight = aHeight / 2;

        // Compute boundaries for rectangle A
        float aLeft = aPos.x - aHalfWidth;
        float aRight = aPos.x + aHalfWidth;
        float aTop = aPos.y - aHalfHeight;  // Smaller y is the top
        float aBottom = aPos.y + aHalfHeight; // Larger y is the bottom

        float bHalfWidth = bWidth / 2;
        float bHalfHeight = bHeight / 2;

        // Compute the four corners of rectangle B
        Vector2f[] bCorners = new Vector2f[]{
                new Vector2f(bPos.x - bHalfWidth, bPos.y - bHalfHeight), // Top-left
                new Vector2f(bPos.x + bHalfWidth, bPos.y - bHalfHeight), // Top-right
                new Vector2f(bPos.x - bHalfWidth, bPos.y + bHalfHeight), // Bottom-left
                new Vector2f(bPos.x + bHalfWidth, bPos.y + bHalfHeight)  // Bottom-right
        };

        // Check if any corner of B is inside A
        for (Vector2f corner : bCorners) {
            if (corner.x >= aLeft && corner.x <= aRight && corner.y >= aTop && corner.y <= aBottom) {
                return true; // Collision detected
            }
        }

        return false; // No collision
    }




}
