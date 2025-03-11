package Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class KeyboardInput {

    public interface ICommand {
        void invoke(double elapsedTime);
    }

    public KeyboardInput(long window) {
        this.window = window;
    }

    public void registerCommand(int key, boolean keyPressOnly, ICommand callback) {
        commandEntries.computeIfAbsent(key, k -> new ArrayList<>())
                .add(new CommandEntry(key, keyPressOnly, false, callback, null));
        keysPressed.put(key, false);
    }

    public void registerReleaseCommand(int key, ICommand callback) {
        commandEntries.computeIfAbsent(key, k -> new ArrayList<>())
                .add(new CommandEntry(key, false, true, null, callback));
        keysPressed.put(key, false);
    }

    public void registerBothCommand(int key, ICommand pressCallback, ICommand releaseCallback) {
        commandEntries.computeIfAbsent(key, k -> new ArrayList<>())
                .add(new CommandEntry(key, true, true, pressCallback, releaseCallback));
        keysPressed.put(key, false);
    }

    public void update(double elapsedTime) {
        for (var entry : commandEntries.entrySet()) {
            int key = entry.getKey();
            List<CommandEntry> commands = entry.getValue();
            boolean isPressed = glfwGetKey(window, key) == GLFW_PRESS;
            boolean wasPressed = keysPressed.getOrDefault(key, false);

            for (CommandEntry command : commands) {
                if (command.keyPressOnly && isKeyNewlyPressed(key) && command.pressCallback != null) {
                    command.pressCallback.invoke(elapsedTime);
                }
                if (!command.keyPressOnly && !command.keyReleaseOnly && isPressed && command.pressCallback != null) {
                    command.pressCallback.invoke(elapsedTime);
                }
                if (command.keyReleaseOnly && isKeyNewlyReleased(key) && command.releaseCallback != null) {
                    command.releaseCallback.invoke(elapsedTime);
                }
            }

            keysPressed.put(key, isPressed);
        }
    }

    private boolean isKeyNewlyPressed(int key) {
        return (glfwGetKey(window, key) == GLFW_PRESS) && !keysPressed.getOrDefault(key, false);
    }

    private boolean isKeyNewlyReleased(int key) {
        return (glfwGetKey(window, key) == GLFW_RELEASE) && keysPressed.getOrDefault(key, false);
    }

    private final long window;
    private final HashMap<Integer, List<CommandEntry>> commandEntries = new HashMap<>();
    private final HashMap<Integer, Boolean> keysPressed = new HashMap<>();

    private record CommandEntry(int key, boolean keyPressOnly, boolean keyReleaseOnly,
                                ICommand pressCallback, ICommand releaseCallback) {}

    public HashMap<Integer, Boolean> getKeysPressed() {
        return keysPressed;
    }

    public void clear() {
        commandEntries.clear();
        keysPressed.clear();
    }
}
