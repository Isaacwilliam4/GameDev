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
    private final Random random = new Random();
    private List<Entity> entitiesToRemove = new ArrayList<>();
    private double timeSinceLastSpawn = 0.0;
    private float[] CAR_X_VALS = new float[]{-.8f, -.47f, -.15f, 0.17f, .5f};

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

            if (position.getY() > 1.0f) {
                entitiesToRemove.add(entity);
            }
        }

    }

    public void cleanUp(){
        for (var entity : entitiesToRemove) {
            entity.remove(Position.class);
        }
    }

    public void clearEntitiesToRemove(){
        entitiesToRemove.clear();
    }

    private void spawnCar() {
        int idx = random.nextInt(CAR_X_VALS.length);
        float x = CAR_X_VALS[idx];
        var car = CarEntity.create(x, CAR_START_Y);
        entities.put(car.getId(), car); // Add car to the system
    }

    public Map<Long, Entity> getCars(){
        return entities;
    }

    public List<Entity> getEntitiesToRemove() {
        return entitiesToRemove;
    }


}
