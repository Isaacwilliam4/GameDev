package ecs.Entities;

import edu.usu.graphics.Texture;
import edu.usu.graphics.Color;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Snake {
    public static Entity create(Texture square, int x, int y) {
        final double MOVE_INTERVAL = .150; // seconds

        var snake = new Entity();

        snake.add(new ecs.Components.Appearance(square, Color.WHITE));
        snake.add(new ecs.Components.Position(x, y));
        snake.add(new ecs.Components.Collision());
        snake.add(new ecs.Components.Movable(ecs.Components.Movable.Direction.Stopped, MOVE_INTERVAL));

        return snake;
    }

    public static void enableControls(Entity snake) {
        snake.add(new ecs.Components.KeyboardControlled(
                Map.of(
                        GLFW_KEY_UP, ecs.Components.Movable.Direction.Up,
                        GLFW_KEY_DOWN, ecs.Components.Movable.Direction.Down,
                        GLFW_KEY_LEFT, ecs.Components.Movable.Direction.Left,
                        GLFW_KEY_RIGHT, ecs.Components.Movable.Direction.Right
                )));
    }
}
