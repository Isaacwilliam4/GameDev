package ecs.Systems;

import ecs.Entities.CarEntity;
import ecs.Components.Position;
import ecs.Entities.Entity;
import edu.usu.graphics.Graphics2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CarSystem extends System {

    private final float SPAWN_INTERVAL = 1.5f; // Time between car spawns
    private final float CAR_SPEED = 0.3f; // Speed of cars moving downward
    private final float CAR_START_Y = -1.0f; // Spawn at the top
    private final float CAR_MIN_X = -0.7f; // Left side spawn
    private final float CAR_MAX_X = 0.7f; // Right side spawn
    private final Random random = new Random();
    private List<Entity> entitiesToRemove = new ArrayList<>();
    private double timeSinceLastSpawn = 0.0;

    public CarSystem() {
        super(Position.class);
    }

    @Override
    public void update(double elapsedTime) {
        timeSinceLastSpawn += elapsedTime;

        // Spawn new cars at a fixed interval
        if (timeSinceLastSpawn >= SPAWN_INTERVAL) {
            timeSinceLastSpawn = 0.0;
            spawnCar();
        }

        // Move existing cars downward
        for (var entity : entities.values()) {
            var position = entity.get(Position.class);
            var newPostion = new Position(position.getX(), position.getY() + CAR_SPEED * (float) elapsedTime, position.rotation);

            entity.remove(Position.class);
            entity.add(newPostion);
            // Remove cars that go off-screen
            if (position.getY() < -1.0f) {
                entitiesToRemove.add(entity);
            }
        }

        for (var entity : entitiesToRemove) {
            entities.remove(entity.getId());
        }
    }

    private void spawnCar() {
        float x = CAR_MIN_X + random.nextFloat() * (CAR_MAX_X - CAR_MIN_X); // Random X position
        var car = CarEntity.create(x, CAR_START_Y);
        entities.put(car.getId(), car); // Add car to the system
    }

    public Map<Long, Entity> getCars(){
        return entities;
    }
}
