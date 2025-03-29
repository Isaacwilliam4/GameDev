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
            moveEntity(entity);
        }
    }

    private void moveEntity(ecs.Entities.Entity entity) {
        var movable = entity.get(ecs.Components.Movable.class);

        switch (movable.pendingMove) {
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
        movable.pendingMove = Movable.Direction.Stopped;
    }

    private void move(ecs.Entities.Entity entity, int xIncrement, int yIncrement) {
        var positionComponent = entity.get(ecs.Components.Position.class);
        var position = positionComponent.position;
        positionComponent.previousPositions.add(position);
        positionComponent.position = new Vector2i(position.x + xIncrement, position.y + yIncrement);

        if (entity.contains(ecs.Components.ParticleSystemComponent.class)){
            var particleSystemComponent = entity.get(ecs.Components.ParticleSystemComponent.class);
            particleSystemComponent.center.x += xIncrement;
            particleSystemComponent.center.y += yIncrement;
        }
    }
}
