package ecs.Systems;

import ecs.Components.Movable;
import org.joml.Vector2f;

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

            if (glfwGetKey(window, input.lookup.get(Movable.Move.UP)) == GLFW_PRESS) {
                var gameTimeF = (float) gameTime;
                Vector2f accelerationDt = new Vector2f(movable.acceleration).mul(gameTimeF);
                Vector2f velocityDt = new Vector2f(0,0);
                velocityDt.add(accelerationDt);
                velocityDt.mul(gameTimeF);
                movable.velocity.add(velocityDt);
            }
            if (glfwGetKey(window, input.lookup.get(Movable.Move.ROTATE_LEFT)) == GLFW_PRESS) {
                movable.rotation = -movable.rotationSpeed;
            }
            if (glfwGetKey(window, input.lookup.get(Movable.Move.ROTATE_RIGHT)) == GLFW_PRESS) {
                movable.rotation = movable.rotationSpeed;
            }
        }
    }
}
