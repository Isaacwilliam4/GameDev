package ecs.Systems;

import ecs.Components.Movable;
import ecs.Components.KeyboardControlled;
import ecs.Entities.Entity;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardInput extends System {

    private final long window;
    private final Map<Entity, Map<Integer, Boolean>> entityKeyStates = new HashMap<>();

    public KeyboardInput(long window) {
        super(KeyboardControlled.class);
        this.window = window;
    }

    @Override
    public void update(double gameTime) {
        for (var entity : entities.values()) {
            var movable = entity.get(Movable.class);
            var input = entity.get(KeyboardControlled.class);

            if (movable != null && input != null) {
                // Initialize key state map if it doesn't exist
                entityKeyStates.putIfAbsent(entity, new HashMap<>());
                var keyStates = entityKeyStates.get(entity);

                // Process all direction inputs
                for (Movable.Direction direction : Movable.Direction.values()) {
                    if (input.lookup.containsKey(direction)) {
                        int key = input.lookup.get(direction);
                        boolean isPressed = glfwGetKey(window, key) == GLFW_PRESS;
                        boolean wasPressed = keyStates.getOrDefault(key, false);

                        movable.pendingMove = input.keys.get(key);

                        keyStates.put(key, isPressed);
                    }
                }
            }
        }
    }
}
