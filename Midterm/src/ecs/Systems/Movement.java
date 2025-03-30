package ecs.Systems;

import ecs.Components.Movable;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * This system is responsible for handling the movement of any
 * entity with movable & position components.
 */
public class Movement extends System {
    public Movement() {
        super(ecs.Components.Movable.class, ecs.Components.Position.class);
    }

    private final float MAX_LEFT = -0.71f;
    private final float MAX_RIGHT = 0.71f;

    @Override
    public void update(double elapsedTime) {
        for (var entity : entities.values()) {
            moveEntity(entity);
        }
    }

    private void moveEntity(ecs.Entities.Entity entity) {
        var movable = entity.get(ecs.Components.Movable.class);

        switch (movable.pendingMove) {
            case Movable.Direction.Left:
                move(entity, -movable.moveDist, 0);
                break;
            case Movable.Direction.Right:
                move(entity, movable.moveDist, 0);
                break;
        }
        movable.pendingMove = Movable.Direction.Stopped;
    }

    private void move(ecs.Entities.Entity entity, float xIncrement, float yIncrement) {
        var positionComponent = entity.get(ecs.Components.Position.class);
        var position = positionComponent.position;
        positionComponent.previousPositions.add(position);
        if (position.x + xIncrement > MAX_LEFT && position.x + xIncrement < MAX_RIGHT) {
            positionComponent.position = new Vector2f(position.x + xIncrement, position.y + yIncrement);
            if (entity.contains(ecs.Components.ParticleSystemComponent.class)){
                var particleSystemComponent = entity.get(ecs.Components.ParticleSystemComponent.class);
                particleSystemComponent.center = positionComponent.position;
            }
        }
    }
}
