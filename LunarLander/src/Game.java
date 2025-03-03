import edu.usu.graphics.*;
import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private int mazeSize = 3;
    private float CELL_SIZE;
    private final float CELL_WALL_THICKNESS = 0.002f;
    private final float MAZE_LEFT = -0.5f;
    private final float MAZE_TOP = -0.5f;
    private final float MENU_LEFT = -0.2f;
    private final float MENU_TOP = -0.2f;
    private final int STEP_ON_SHORTEST_PATH_SCORE = 5;
    private final float TEXT_HEIGHT = 0.038f;
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color MAZE_COLOR = Color.WHITE;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    private final String instructionText;
    private MazeCell[][] maze;
    private Rectangle rectCircle;
    private Rectangle rectCircleEnd;
    private final Rectangle displayRect = new Rectangle(MAZE_LEFT, MAZE_TOP, 2*(Math.abs(MAZE_LEFT)), 2*(Math.abs(MAZE_LEFT)), -1.0f);
    private Texture character;
    private Texture endSignal;
    private Texture bg;
    private Texture breadCrumb;
    private Texture hint;
    private Font font;
    private MazeCell characterLocation;
    private MazeCell previousLocation;
    private final KeyboardInput inputKeyboard;
    private MazeCell startLocation;
    private MazeCell endLocation;
    private boolean showHint = false;
    private boolean showBreadCrumbs = false;
    private boolean showPath = false;
    private double timePassed = 0;
    private float score = 0;
    private List<String> scoreList = new ArrayList<>();
    private Set<MazeCell> originalShortestPath;
    private GameState gameState = GameState.MENU;
    private Menu menuSelect = Menu.PLAYGAME;
    private List<Vector2f> terrain;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.inputKeyboard = new KeyboardInput(graphics.getWindow());
        this.instructionText = "5x5 Maze - f1\n" +
                "10x10 Maze - f2\n" +
                "15x15 Maze - f3\n" +
                "20x20 Maze - f4\n" +
                "Display High Scores - f5\n" +
                "Display Credits - f6\n" +
                "Go to Menu - Backspace";
    }

    public void initialize() {
        endSignal = new Texture("resources/images/galaxy.png");
        character = new Texture("resources/images/alien.png");
        breadCrumb = new Texture("resources/images/star.png");
        hint = new Texture("resources/images/rocket.png");
        bg = new Texture("resources/images/spacebg.png");
        font = new Font("resources/fonts/Blacknorthdemo-mLE25.otf", 42, false);
        registerKeys();
    }

    public void startGame(){
        score = 0;
        timePassed = 0;
        initTerrain();
    }

    private void initTerrain() {
        List<Vector2f> terrain = new ArrayList<>();
        terrain.add(new Vector2f(-1, 0));
        terrain.add(new Vector2f(1, 0));
        this.terrain = GameUtils.splitTerrain(terrain,  0.1f, 0.1f);
    }
    private void registerKeys() {
        inputKeyboard.registerCommand(GLFW_KEY_ENTER, true, (double elapsedTime) -> {
            if (gameState == GameState.MENU){
                switch (menuSelect) {
                    case PLAYGAME -> {
                        gameState = GameState.PLAYGAME;
                        startGame();
                    }
                    case HIGHSCORES -> gameState = GameState.HIGHSCORES;
                    case CUSTOMIZECONTROLS -> gameState = GameState.CUSTOMIZECONTROLS;
                    case CREDITS -> gameState = GameState.CREDITS;
                }
            }
        });
        // Register the inputs we want to have invoked
        inputKeyboard.registerCommand(GLFW_KEY_W, true, (double elapsedTime) -> {
            if (gameState == GameState.MENU) {
                menuSelect = menuSelect.previous();
            }
            else{
                makeMove(Movement.UP);
            }
        });
        inputKeyboard.registerCommand(GLFW_KEY_S, true, (double elapsedTime) -> {
            if (gameState == GameState.MENU) {
                menuSelect = menuSelect.next();
            }
            else{
                makeMove(Movement.DOWN);
            }
        });
        inputKeyboard.registerCommand(GLFW_KEY_A, true, (double elapsedTime) -> {
            makeMove(Movement.LEFT);
        });
        inputKeyboard.registerCommand(GLFW_KEY_D, true, (double elapsedTime) -> {
            makeMove(Movement.RIGHT);
        });
        inputKeyboard.registerCommand(GLFW_KEY_I, true, (double elapsedTime) -> {
            makeMove(Movement.UP);
        });
        inputKeyboard.registerCommand(GLFW_KEY_K, true, (double elapsedTime) -> {
            makeMove(Movement.DOWN);
        });
        inputKeyboard.registerCommand(GLFW_KEY_J, true, (double elapsedTime) -> {
            makeMove(Movement.LEFT);
        });
        inputKeyboard.registerCommand(GLFW_KEY_L, true, (double elapsedTime) -> {
            makeMove(Movement.RIGHT);
        });
        inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> {
            if (gameState == GameState.MENU) {
                menuSelect = menuSelect.previous();
            }
            else{
                makeMove(Movement.UP);
            }
        });
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> {
            if (gameState == GameState.MENU) {
                menuSelect = menuSelect.next();
            }
            else{
                makeMove(Movement.DOWN);
            }
        });
        inputKeyboard.registerCommand(GLFW_KEY_LEFT, true, (double elapsedTime) -> {
            makeMove(Movement.LEFT);
        });
        inputKeyboard.registerCommand(GLFW_KEY_RIGHT, true, (double elapsedTime) -> {
            makeMove(Movement.RIGHT);
        });

        inputKeyboard.registerCommand(GLFW_KEY_ESCAPE, true, (double elapsedTime) -> {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        });
        inputKeyboard.registerCommand(GLFW_KEY_P, true, (double elapsedTime) -> {
            showPath = !showPath;
        });

        inputKeyboard.registerCommand(GLFW_KEY_B, true, (double elapsedTime) -> {
            showBreadCrumbs = !showBreadCrumbs;
        });

        inputKeyboard.registerCommand(GLFW_KEY_H, true, (double elapsedTime) -> {
            showHint = !showHint;
        });

        inputKeyboard.registerCommand(GLFW_KEY_BACKSPACE, true, (double elapsedTime) -> {
            gameState = GameState.MENU;
        });

        inputKeyboard.registerCommand(GLFW_KEY_F1, true, (double elapsedTime) -> {
            gameState = GameState.PLAYGAME;
            mazeSize = 5;
            startGame();
        });

        inputKeyboard.registerCommand(GLFW_KEY_F2, true, (double elapsedTime) -> {
            gameState = GameState.PLAYGAME;
            mazeSize = 10;
            startGame();
        });

        inputKeyboard.registerCommand(GLFW_KEY_F3, true, (double elapsedTime) -> {
            gameState = GameState.PLAYGAME;
            mazeSize = 15;
            startGame();
        });

        inputKeyboard.registerCommand(GLFW_KEY_F4, true, (double elapsedTime) -> {
            gameState = GameState.PLAYGAME;
            mazeSize = 50;
            startGame();
        });

        inputKeyboard.registerCommand(GLFW_KEY_F5, true, (double elapsedTime) -> {
            gameState = GameState.HIGHSCORES;
        });

        inputKeyboard.registerCommand(GLFW_KEY_F6, true, (double elapsedTime) -> {
            gameState = GameState.CREDITS;
        });
    }

    private void makeMove(Movement move){
        if (gameState != GameState.MENU) {
        }
    }



    public void shutdown() {
        character.cleanup();
        endSignal.cleanup();
        bg.cleanup();
        breadCrumb.cleanup();
        hint.cleanup();
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
        glfwPollEvents();
        inputKeyboard.update(elapsedTime);
    }

    private void update(double elapsedTime) {
        if (gameState == GameState.PLAYGAME){
            timePassed += elapsedTime;

        }
    }

    private void drawTextWithNewLines(String text, float top, float left, float height) {
        String[] stringArr = text.split("\n");

        int idx = 0;
        for (String str: stringArr){
            float newTop = top + (idx * height);
            graphics.drawTextByHeight(font, str, left, newTop, height, TEXT_COLOR);
            idx++;
        }
    }

    private void drawMenu(String text, float top, float left, float height) {
        String[] stringArr = text.split("\n");

        int idx = 0;
        for (String str: stringArr){
            float newTop = top + (idx * height);
            if (menuSelect.ordinal() == idx){
                graphics.drawTextByHeight(font, str, left, newTop, height, Color.YELLOW);
            }
            else{
                graphics.drawTextByHeight(font, str, left, newTop, height, TEXT_COLOR);
            }
            idx++;
        }
    }

    private void drawMenu(){
        String menuText =
                """
                    Start a New Game
                    View High Scores
                    Customize Controls
                    View Credits
                """;

        StringBuilder menuBuilder = new StringBuilder();
        menuBuilder.append(menuText);
        drawMenu(menuBuilder.toString(), MENU_TOP, MENU_LEFT, TEXT_HEIGHT);
    }

    private void renderTerrain(){

        for (int i = 0; i < terrain.size() - 1; i++){
            Vector2f pt1 = terrain.get(i);
            Vector2f pt2 = terrain.get(i+1);

            Vector3f tpt1 = new Vector3f(pt1.x, pt1.y, 0);
            Vector3f tpt2 = new Vector3f(pt2.x, pt2.y, 0);
            Vector3f tpt3 = new Vector3f(pt1.x, 1, 0);
            Triangle t1 = new Triangle(tpt1, tpt2, tpt3);

            Vector3f t2pt1 = new Vector3f(pt1.x, 1, 0);
            Vector3f t2pt2 = new Vector3f(pt2.x, pt2.y, 0);
            Vector3f t2pt3 = new Vector3f(pt2.x, 1, 0);
            Triangle t2 = new Triangle(t2pt1, t2pt2, t2pt3);

            graphics.draw(t1, Color.YELLOW);
            graphics.draw(t2, Color.YELLOW);
        }
    }

    private void render(long window, double elapsedTime) {
        graphics.begin();

        switch (gameState) {
            case MENU -> {
                drawMenu();
            }
            case PLAYGAME -> {
                renderTerrain();
            }
        }
        graphics.end();
    }

}
