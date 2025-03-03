import edu.usu.graphics.*;
import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import org.joml.Vector2f;

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
        initMaze();
        MazeUtils.setupMaze(maze);
        originalShortestPath = new HashSet<>(MazeUtils.updateShortestPath(maze, characterLocation, endLocation));
        originalShortestPath.remove(characterLocation);
    }

    private void initMaze() {
        maze = new MazeCell[mazeSize][mazeSize];
        for (int x = 0; x < mazeSize; x++) {
            for (int y = 0; y < mazeSize; y++) {
                maze[x][y] = new MazeCell(x,y);
            }
        }
        startLocation = maze[0][0];
        characterLocation = maze[0][0];
        characterLocation.setVisited(true);
        endLocation = maze[maze.length - 1][maze[0].length - 1];
        CELL_SIZE = 1/ (float) mazeSize;
        rectCircle = new Rectangle(MAZE_LEFT, MAZE_TOP, CELL_SIZE, CELL_SIZE);
        float bottom = Math.abs(MAZE_TOP) - CELL_SIZE;
        float right = Math.abs(MAZE_LEFT) - CELL_SIZE;
        rectCircleEnd = new Rectangle(right, bottom, CELL_SIZE, CELL_SIZE);
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
            previousLocation = characterLocation;
            switch(move){
                case UP -> {
                    if ((characterLocation.getRow() - 1) >= 0 &&
                            characterLocation.getTop() != null){
                        rectCircle.top = rectCircle.top - CELL_SIZE;
                        characterLocation = maze[characterLocation.getRow()-1][characterLocation.getColumn()];
                    }
                }
                case DOWN -> {
                    if ((characterLocation.getRow() + 1) < mazeSize &&
                            characterLocation.getBottom() != null){
                        rectCircle.top = rectCircle.top + CELL_SIZE;
                        characterLocation = maze[characterLocation.getRow()+1][characterLocation.getColumn()];
                    }
                }
                case LEFT -> {
                    if ((characterLocation.getColumn() - 1) >= 0 &&
                            characterLocation.getLeft() != null){
                        rectCircle.left = rectCircle.left - CELL_SIZE;
                        characterLocation = maze[characterLocation.getRow()][characterLocation.getColumn()-1];
                    }
                }
                case RIGHT -> {
                    if ((characterLocation.getColumn() + 1) < mazeSize &&
                            characterLocation.getRight() != null){
                        rectCircle.left = rectCircle.left + CELL_SIZE;
                        characterLocation = maze[characterLocation.getRow()][characterLocation.getColumn()+1];
                    }
                }
            }

            previousLocation.setScoreComputed(false);
            if (originalShortestPath.contains(characterLocation)) {
                score += STEP_ON_SHORTEST_PATH_SCORE;
                originalShortestPath.remove(characterLocation);
            }
            else{
                if (!characterLocation.equals(startLocation) &
                        !characterLocation.isScoreComputed() &
                        !characterLocation.isVisited()) {
                    score -= 1;
                }
            }
            characterLocation.setVisited(true);
            characterLocation.setScoreComputed(true);
            MazeUtils.updateShortestPath(maze, characterLocation, endLocation);

            if (characterLocation.getIndex().equals(endLocation.getIndex())) {
                scoreList.add("Score: " + Float.toString(score) + ",    Maze Size: " + Integer.toString(mazeSize) + "\n");
                gameState = GameState.ENDGAME;
            }
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

    private void render(long window, double elapsedTime) {
        graphics.begin();

        switch (gameState){
            case MENU -> {
                drawMenu();
            }
            case ENDGAME -> {
                StringBuilder menuBuilder = new StringBuilder();
                menuBuilder.append("Finished Maze\nScore:")
                        .append(score)
                        .append("\nTime:")
                        .append(decimalFormat.format(timePassed))
                        .append("\n\n");
                menuBuilder.append(instructionText);
                drawTextWithNewLines(menuBuilder.toString(), MENU_TOP, MENU_LEFT, TEXT_HEIGHT);
            }
            case PLAYGAME -> {
                graphics.draw(bg, displayRect, Color.WHITE);

                for (var row: maze){
                    for (var cell:row){
                        renderCell(cell);
                    }
                }

                StringBuilder scoreListBuilder = new StringBuilder();
                scoreListBuilder.append("High Scores: \n");

                for (String score: scoreList){
                    scoreListBuilder.append(score);
                }

                String timeAndScoreText = "Time " + decimalFormat.format(timePassed)  + "\n" +
                        "Score " + score;

                drawTextWithNewLines(instructionText, -0.5f, -0.97f, TEXT_HEIGHT);
                drawTextWithNewLines(timeAndScoreText, -0.5f, 0.55f, TEXT_HEIGHT);
                drawTextWithNewLines(scoreListBuilder.toString(), -0.1f, -0.97f, TEXT_HEIGHT);

                graphics.draw(character, rectCircle, 0, new Vector2f(rectCircle.left + rectCircle.width / 2, rectCircle.top + rectCircle.height / 2), Color.WHITE);
                graphics.draw(endSignal, rectCircleEnd, 0, new Vector2f(rectCircle.left + rectCircle.width / 2, rectCircle.top + rectCircle.height / 2), Color.WHITE);
            }
            case CREDITS -> {
                graphics.drawTextByHeight(font, "Made by Isaac Peterson", MENU_LEFT, MENU_TOP, TEXT_HEIGHT, TEXT_COLOR);
            }
            case HIGHSCORES -> {
                StringBuilder scoreListBuilder = new StringBuilder();
                scoreListBuilder.append("High Scores: \n");

                for (String score: scoreList){
                    scoreListBuilder.append(score);
                }
                drawTextWithNewLines(scoreListBuilder.toString(), MENU_TOP, MENU_LEFT, TEXT_HEIGHT);
            }
        }
        graphics.end();
    }

    private void renderCell(MazeCell cell){
        //Update cell's maze walls
        if (cell.getTop() == null){
            float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS);

            graphics.draw(r, MAZE_COLOR);

        }
        if (cell.getBottom() == null){
            float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE;
            float top = MAZE_TOP + (cell.getRow() + 1) * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS);

            graphics.draw(r, MAZE_COLOR);

        }
        if (cell.getLeft() == null){
            float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE);

            graphics.draw(r, MAZE_COLOR);
        }
        if (cell.getRight() == null){
            float left = MAZE_LEFT + (cell.getColumn() + 1) * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE);

            graphics.draw(r, MAZE_COLOR);
        }

        //Only update the cell if its not the character's cell or the end cell
        if (!cell.getIndex().equals(characterLocation.getIndex()) &
            !cell.getIndex().equals(endLocation.getIndex())){
            boolean showShortestPath = cell.isOnShortestPath() & showPath;
            boolean showNeighborHint = cell.isOnShortestPath() &
                    MazeUtils.areNeighbors(cell, characterLocation) &
                    !showShortestPath &
                    showHint;
            boolean drawBreadCrumb = cell.isVisited() & showBreadCrumbs & !showNeighborHint & !showShortestPath;

            float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE + (1.0f/4.0f)*CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE + (1.0f/4.0f)*CELL_SIZE;
            float size = (1.0f/2.0f)*CELL_SIZE;

            if (drawBreadCrumb){
                Rectangle r = new Rectangle(left, top, size, size);
                graphics.draw(breadCrumb, r, Color.WHITE);
            }
            else if (showNeighborHint | showShortestPath){
                Rectangle r = new Rectangle(left, top, size, size);
                graphics.draw(hint, r, Color.WHITE);
            }
        }


    }
}
