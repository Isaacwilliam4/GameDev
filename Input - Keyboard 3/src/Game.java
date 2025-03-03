import edu.usu.graphics.*;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private final KeyboardInput inputKeyboard;

    private final Rectangle rectSessler = new Rectangle(-0.85f, -0.45f, 0.40f, 0.40f);
    private Texture texSessler;
    private float rotation = 0.0f;

    private static final float SPRITE_MOVE_RATE_PER_SECOND = 0.40f;    // world coords per second
    private static final float SPRITE_ROTATE_RATE_PER_SECOND = (float) (Math.PI / 2); // radians per second

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.inputKeyboard = new KeyboardInput(graphics.getWindow());
    }

    public void initialize() {
        texSessler = new Texture("resources/images/sessler.jpg");

        // Register the inputs we want to have invoked
        inputKeyboard.registerCommand(GLFW_KEY_W, false, (double elapsedTime) -> {
            moveUp((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_S, false, (double elapsedTime) -> {
            moveDown((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_A, false, (double elapsedTime) -> {
            moveLeft((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_D, false, (double elapsedTime) -> {
            moveRight((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });

        inputKeyboard.registerCommand(GLFW_KEY_UP, false, (double elapsedTime) -> {
            moveUp((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, false, (double elapsedTime) -> {
            moveDown((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_LEFT, false, (double elapsedTime) -> {
            moveLeft((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_RIGHT, false, (double elapsedTime) -> {
            moveRight((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });

        inputKeyboard.registerCommand(GLFW_KEY_E, false, (double elapsedTime) -> {
            rotateRight((float) elapsedTime * SPRITE_ROTATE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_Q, false, (double elapsedTime) -> {
            rotateLeft((float) elapsedTime * SPRITE_ROTATE_RATE_PER_SECOND);
        });

        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        });
    }

    public void shutdown() {
        texSessler.cleanup();
    }

    public void run() {
        // Grab the first time
        double previousTime = glfwGetTime();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - previousTime;    // elapsed time is in seconds
            previousTime = currentTime;

            processInput(elapsedTime);
            update(elapsedTime);
            render(graphics.getWindow(), elapsedTime);
        }
    }

    private void processInput(double elapsedTime) {
        // Poll for window events: required in order for window, keyboard, etc events are captured.
        // Question: Should this go in the KeyboardInput...I don't think so because it polls for all types of events, not just keyboard
        glfwPollEvents();

        inputKeyboard.update(elapsedTime);
    }

    private void update(double elapsedTime) {
    }

    private void render(long window, double elapsedTime) {
        graphics.begin();

        graphics.draw(texSessler, rectSessler, rotation, new Vector2f(rectSessler.left + rectSessler.width / 2, rectSessler.top + rectSessler.height / 2), Color.WHITE);

        graphics.end();
    }

    private void moveUp(float distance) {
        if (rectSessler.top > -0.5f) {
            rectSessler.top = Math.max(rectSessler.top - distance, -0.5f);
        }
    }

    private void moveDown(float distance) {
        if ((rectSessler.top + rectSessler.height) < 0.5f) {
            rectSessler.top = Math.min(rectSessler.top + distance, 0.5f);
        }
    }

    private void moveLeft(float distance) {
        if (rectSessler.left > -0.90f) {
            rectSessler.left = Math.max(rectSessler.left - distance, -0.90f);
        }
    }

    private void moveRight(float distance) {
        if ((rectSessler.left + rectSessler.width) < 0.90f) {
            rectSessler.left = Math.min(rectSessler.left + distance, 0.90f);
        }
    }

    private void rotateRight(float angle) {
        rotation += angle;
    }

    private void rotateLeft(float angle) {
        rotation -= angle;
    }
}
