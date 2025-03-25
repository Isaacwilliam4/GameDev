package ecs.Systems;

import ecs.Components.Movable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

/**
 * This system is responsible for handling the movement of any
 * entity with movable & position components.
 */
public class Movement extends System {
    public Movement() {
        super(ecs.Components.Movable.class, ecs.Components.Position.class);
    }

    @Override
    public void update(double elapsedTime) {
        for (var entity : entities.values()) {
            moveEntity(entity, elapsedTime);
        }
    }

    private void moveEntity(ecs.Entities.Entity entity, double elapsedTime) {
        var movable = entity.get(ecs.Components.Movable.class);
//        movable.elapsedInterval += elapsedTime;
        move(entity, movable.velocity.x, movable.velocity.y, movable.rotation);

    }

    private void move(ecs.Entities.Entity entity, float xIncrement, float yIncrement, float rotation) {
        var position = entity.get(ecs.Components.Position.class);
        position.position.add(new Vector3f(xIncrement, yIncrement, rotation));
    }
}
