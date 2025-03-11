package ecs.Entities;

import ecs.Components.Movable;
import edu.usu.graphics.Color;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;

import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class Ship {
    public static Entity create(Texture texture, float x, float y, float rotation){
        var ship = new Entity();
        ship.add(new ecs.Components.Appearance(texture, Color.WHITE));
        ship.add(new ecs.Components.Position(x, y, rotation));
        ship.add(new ecs.Components.Collision());
        ship.add(new ecs.Components.Movable(new Vector2f(0, 0),
                                            new Vector2f(0, 0),
                                            0,
                                            0.001f));
        return ship;
    }

    public static void enableControls(Entity snake) {
        snake.add(new ecs.Components.KeyboardControlled(
                Map.of(
                        GLFW_KEY_UP, Movable.Move.UP,
//                        GLFW_KEY_DOWN, ecs.Components.Movable.Direction.Down,
                        GLFW_KEY_LEFT, Movable.Move.ROTATE_LEFT,
                        GLFW_KEY_RIGHT, Movable.Move.ROTATE_RIGHT
                )));
    }
}
