package ecs.Entities;

import ecs.Components.ParticleSystemComponent;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Texture;
import edu.usu.graphics.Color;
import org.joml.Vector2f;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Snake {
    public static Entity create(Texture square, int x, int y, Graphics2D graphics) {
        final double MOVE_INTERVAL = .150; // seconds

        var snake = new Entity();

        snake.add(new ecs.Components.Appearance(square, Color.WHITE));
        snake.add(new ecs.Components.Position(x, y));
        snake.add(new ecs.Components.Collision());
        snake.add(new ecs.Components.Movable(MOVE_INTERVAL));

        Texture tex = new Texture("resources/images/square.png");

        snake.add(new ParticleSystemComponent(
                graphics,
                tex,
                new Vector2f(0, 0),  // Center at character position
                new Vector2f(0, 1),   // Direction (can be adjusted for effects)
                0.05f, 0.02f,         // Size Mean & StdDev
                0.1f, 0.05f,          // Speed Mean & StdDev
                1.0f, 0.5f,           // Lifetime Mean & StdDev
                0.2f,                  // Angle StdDev
                0.1
        ));

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
