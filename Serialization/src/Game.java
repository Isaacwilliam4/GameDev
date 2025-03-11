import edu.usu.graphics.*;

import static org.lwjgl.glfw.GLFW.*;
import java.text.SimpleDateFormat;

public class Game {
    private final Graphics2D graphics;
    private final KeyboardInput inputKeyboard;

    private static final String SAVE_MESSAGE = "F1 - Save Something";
    private static final String LOAD_MESSAGE = "F2 - Load Something";
    private Font fontDisplay;

    private final Serializer serializer;

    private GameState gameState;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.inputKeyboard = new KeyboardInput(graphics.getWindow());
        this.serializer = new Serializer();
    }

    public void initialize() {
        fontDisplay = new Font("resources/fonts/Roboto-Regular.ttf", 48, true);

        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        });

        inputKeyboard.registerCommand(GLFW_KEY_F1, true, (double elapsedTime) -> {
            this.serializer.saveGameState(new GameState(10000, 20));
        });
        inputKeyboard.registerCommand(GLFW_KEY_F2, true, (double elapsedTime) -> {
            this.gameState = new GameState();
            this.serializer.loadGameState(this.gameState);
        });
    }

    public void shutdown() {
        this.serializer.shutdown();
    }

    public void run() {
        // Grab the first time
        double previousTime = glfwGetTime();

        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - previousTime;    // elapsed time is in seconds
            previousTime = currentTime;

            processInput(elapsedTime);
            update(elapsedTime);
            render(elapsedTime);
        }
    }

    private void processInput(double elapsedTime) {
        // Poll for window events: required in order for window, keyboard, etc events are captured.
        glfwPollEvents();

        inputKeyboard.update(elapsedTime);
    }

    private void update(double elapsedTime) {
    }

    private void render(double elapsedTime) {
        graphics.begin();

        float top = -0.5f;
        float height = 0.08f;
        graphics.drawTextByHeight(fontDisplay, SAVE_MESSAGE, -0.85f, top, height, Color.WHITE);

        top += height;
        graphics.drawTextByHeight(fontDisplay, LOAD_MESSAGE, -0.85f, top, height, Color.WHITE);

        if (this.gameState != null && this.gameState.initialized) {
            final float ITEM_HEIGHT = 0.06f;
            top = 0.0f;
            // Center each one
            String name = String.format("Name: %s", this.gameState.name);
            float width = fontDisplay.measureTextWidth(name, ITEM_HEIGHT);
            graphics.drawTextByHeight(fontDisplay, name, 0.0f - (float) width / 2, top, ITEM_HEIGHT, Color.YELLOW);

            top += ITEM_HEIGHT * 1.1f;
            String score = String.format("Score: %d", this.gameState.score);
            width = fontDisplay.measureTextWidth(score, ITEM_HEIGHT);
            graphics.drawTextByHeight(fontDisplay, score, 0.0f - (float) width / 2, top, ITEM_HEIGHT, Color.YELLOW);

            top += ITEM_HEIGHT * 1.1f;
            String level = String.format("Level: %d", this.gameState.level);
            width = fontDisplay.measureTextWidth(level, ITEM_HEIGHT);
            graphics.drawTextByHeight(fontDisplay, level, 0.0f - (float) width / 2, top, ITEM_HEIGHT, Color.YELLOW);

            top += ITEM_HEIGHT * 1.1f;
            String date = String.format("Date: %s", (new SimpleDateFormat("MMM-dd-yyyy hh::mm::ss")).format(this.gameState.timeStamp));
            width = fontDisplay.measureTextWidth(date, ITEM_HEIGHT);
            graphics.drawTextByHeight(fontDisplay, date, 0.0f - (float) width / 2, top, ITEM_HEIGHT, Color.YELLOW);
        }

        graphics.end();
    }
}
