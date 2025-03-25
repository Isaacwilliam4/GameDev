import ecs.Components.Movable;
import ecs.Entities.*;
import ecs.Systems.*;
import ecs.Systems.Countdown;
import ecs.Systems.KeyboardInput;
import edu.usu.graphics.*;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private final int GRID_SIZE = 50;
    private final int OBSTACLE_COUNT = 15;

    private final List<Entity> removeThese = new ArrayList<>();
    private final List<Entity> addThese = new ArrayList<>();
    private Entity ship;

    private ecs.Systems.Renderer sysRenderer;
    private ecs.Systems.Collision sysCollision;
    private ecs.Systems.Movement sysMovement;
    private ecs.Systems.KeyboardInput sysKeyboardInput;
    private ecs.Systems.Countdown sysCountdown;

    public void initialize(Graphics2D graphics) {
        var texSquare = new Texture("resources/images/square-outline.png");
        var lunarLander = new Texture("resources/images/lunarLander.png");


        sysRenderer = new Renderer(graphics, GRID_SIZE);
        sysCollision = new Collision((Entity entity) -> {
            // Remove the exist food
            removeThese.add(entity);
            // Generate another piece of food
            addThese.add(createFood(texSquare));
        });
        sysMovement = new Movement();
        sysKeyboardInput = new KeyboardInput(graphics.getWindow());
        sysCountdown = new Countdown(
                graphics,
                (Entity entity) -> {
                    removeEntity(entity);
                    ecs.Entities.Snake.enableControls(ship);
                    addEntity(ship);
                });

        initializeBorder(texSquare);
        initializeObstacles(texSquare);
        initializeShip(lunarLander);
        addEntity(createFood(texSquare));

        var countdown = ecs.Entities.Countdown.create(3);
        addEntity(countdown);
    }

    public void update(double elapsedTime) {
        // Because ECS framework, input processing is now part of the update
        sysKeyboardInput.update(elapsedTime);
        // Now do the normal update
        sysMovement.update(elapsedTime);
        sysCollision.update(elapsedTime);

        for (var entity : removeThese) {
            removeEntity(entity);
        }
        removeThese.clear();

        for (var entity : addThese) {
            addEntity(entity);
        }
        addThese.clear();

        // Because ECS framework, rendering is now part of the update
        sysRenderer.update(elapsedTime);
        sysCountdown.update(elapsedTime);
    }

    private void addEntity(Entity entity) {
        sysKeyboardInput.add(entity);
        sysMovement.add(entity);
        sysCollision.add(entity);
        sysRenderer.add(entity);
        sysCountdown.add(entity);
    }

    private void removeEntity(Entity entity) {
        sysKeyboardInput.remove(entity.getId());
        sysMovement.remove(entity.getId());
        sysCollision.remove(entity.getId());
        sysRenderer.remove(entity.getId());
        sysCountdown.remove(entity.getId());
    }

    private void initializeBorder(Texture square) {
        for (int position = 0; position < GRID_SIZE; position++) {
            var left = BorderBlock.create(square, 0, position);
            addEntity(left);

            var right = BorderBlock.create(square, GRID_SIZE - 1, position);
            addEntity(right);

            var top = BorderBlock.create(square, position, 0);
            addEntity(top);

            var bottom = BorderBlock.create(square, position, GRID_SIZE - 1);
            addEntity(bottom);
        }
    }

    private void initializeObstacles(Texture square) {
        MyRandom rnd = new MyRandom();
        int remaining = OBSTACLE_COUNT;

        while (remaining > 0) {
            // The 1 and -1 prevent adding obstacles to the border
            int x = (int) rnd.nextRange(1, GRID_SIZE - 1);
            int y = (int) rnd.nextRange(1, GRID_SIZE - 1);
            var proposed = Obstacle.create(square, x, y);
            if (!sysCollision.collidesWithAny(proposed)) {
                addEntity(proposed);
                remaining--;
            }
        }
    }

    private void initializeShip(Texture lunarLander) {
//        MyRandom rnd = new MyRandom();
        boolean done = false;

        while (!done) {
//            int x = (int) rnd.nextRange(1, GRID_SIZE - 1);
//            int y = (int) rnd.nextRange(1, GRID_SIZE - 1);
            var proposed = Ship.create(lunarLander, 0, -.1f, 0);
            if (!sysCollision.collidesWithAny(proposed)) {
                addEntity(proposed);
                ship = proposed;
                done = true;
            }
        }
    }

    private Entity createFood(Texture square) {
        MyRandom rnd = new MyRandom();
        boolean done = false;

        Entity proposed = null;
        while (!done) {
            int x = (int) rnd.nextRange(1, GRID_SIZE - 1);
            int y = (int) rnd.nextRange(1, GRID_SIZE - 1);
            proposed = Food.create(square, x, y);
            if (!sysCollision.collidesWithAny(proposed)) {
                done = true;
            }
        }

        return proposed;
    }

}
