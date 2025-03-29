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
            var aAppearance = a.get(ecs.Components.Appearance.class);
//            var bAppearance = b.get(ecs.Components.Appearance.class);

            if (lineCircleIntersection(aPosition.position, bPosition.position, aPosition.position, aAppearance.width)){
                return true;
            }
        }
        return false;
    }


    private static boolean lineCircleIntersection(Vector2f pt1, Vector2f pt2, Vector2f circleCenter, float circleRadius) {
        pt1 = new Vector2f(pt1.x, pt1.y);
        pt2 = new Vector2f(pt2.x, pt2.y);
        circleCenter = new Vector2f(circleCenter.x, circleCenter.y);
        // Translate points to circle's coordinate system
        Vector2f d = pt2.sub(pt1); // Direction vector of the line
        Vector2f f = pt1.sub(circleCenter); // Vector from circle center to the start of the line

        float a = d.dot(d);
        float b = 2 * f.dot(d);
        float c = f.dot(f) - circleRadius * circleRadius;

        float discriminant = b * b - 4 * a * c;

        // If the discriminant is negative, no real roots and thus no intersection
        if (discriminant < 0) {
            return false;
        }

        // Check if the intersection points are within the segment
        discriminant = (float) Math.sqrt(discriminant);
        float t1 = (-b - discriminant) / (2 * a);
        float t2 = (-b + discriminant) / (2 * a);

        if (t1 >= 0 && t1 <= 1) {
            return true;
        }
        if (t2 >= 0 && t2 <= 1) {
            return true;
        }

        return false;
    }

}
