package ecs.Systems;

import ecs.Components.Movable;
import org.joml.Vector2i;

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
        movable.elapsedInterval += elapsedTime;
        if (movable.elapsedInterval >= movable.moveInterval) {
            movable.elapsedInterval -= movable.moveInterval;
            switch (movable.facing) {
                case Movable.Direction.Up:
                    move(entity, 0, -1);
                    break;
                case Movable.Direction.Down:
                    move(entity, 0, 1);
                    break;
                case Movable.Direction.Left:
                    move(entity, -1, 0);
                    break;
                case Movable.Direction.Right:
                    move(entity, 1, 0);
                    break;
            }
        }
    }

    private void move(ecs.Entities.Entity entity, int xIncrement, int yIncrement) {
        var movable = entity.get(ecs.Components.Movable.class);
        var position = entity.get(ecs.Components.Position.class);

        // Remember current front position, so it can be added back in at the front
        var front = position.segments.get(0);

        // Remove the tail, but only if there aren't new segments to add
        if (movable.segmentsToAdd == 0 && position.segments.size() > 0) {
            position.segments.remove(position.segments.size() - 1);
        } else {
            movable.segmentsToAdd--;
        }

        // Update the front of the entity with the segment moving into the new spot
        Vector2i newFront = new Vector2i(front.x + xIncrement, front.y + yIncrement);
        position.segments.add(0, newFront);
    }
}
