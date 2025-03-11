package ecs.Systems;

import ecs.Components.Movable;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class KeyboardInput extends System {

    private final long window;

    public KeyboardInput(long window) {
        super(ecs.Components.KeyboardControlled.class);

        this.window = window;
    }

    @Override
    public void update(double gameTime) {
        for (var entity : entities.values()) {
            var movable = entity.get(ecs.Components.Movable.class);
            var input = entity.get(ecs.Components.KeyboardControlled.class);

            if (glfwGetKey(window, input.lookup.get(Movable.Direction.Up)) == GLFW_PRESS) {
                if (movable.facing != Movable.Direction.Down) {
                    movable.facing = input.keys.get(GLFW_KEY_UP);
                }
            }
            if (glfwGetKey(window, input.lookup.get(Movable.Direction.Down)) == GLFW_PRESS) {
                if (movable.facing != Movable.Direction.Up) {
                    movable.facing = input.keys.get(GLFW_KEY_DOWN);
                }
            }
            if (glfwGetKey(window, input.lookup.get(Movable.Direction.Left)) == GLFW_PRESS) {
                if (movable.facing != Movable.Direction.Right) {
                    movable.facing = input.keys.get(GLFW_KEY_LEFT);
                }
            }
            if (glfwGetKey(window, input.lookup.get(Movable.Direction.Right)) == GLFW_PRESS) {
                if (movable.facing != Movable.Direction.Left) {
                    movable.facing = input.keys.get(GLFW_KEY_RIGHT);
                }
            }
        }
    }
}
