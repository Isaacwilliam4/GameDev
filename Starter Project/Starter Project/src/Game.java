import com.sun.tools.javac.Main;
import edu.usu.graphics.*;
import edu.usu.graphics.Color;
import edu.usu.graphics.Font;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;

import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;
import java.util.List;
import edu.usu.graphics.*;
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
    private final float TEXT_HEIGHT = 0.04f;
    private final String instructionText;
    private MazeCell[][] maze;
    private Rectangle rectCircle;
    private Rectangle rectCircleEnd;
    private final Rectangle displayRect = new Rectangle(MAZE_LEFT, MAZE_TOP, 2*(Math.abs(MAZE_LEFT)), 2*(Math.abs(MAZE_LEFT)));
    private Texture circle;
    private Texture endCircle;
    private Texture bg;
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
    private GameState gameState = GameState.STARTGAME;

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
        endCircle = new Texture("resources/images/greencircle.png");
        circle = new Texture("resources/images/bluecircle.png");
        bg = new Texture("resources/images/spacebg.png");
        font = new Font("resources/fonts/Blacknorthdemo-mLE25.otf", 42, false);
        registerKeys();
    }

    public void startGame(){
        initMaze();
        setupMaze();
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
        endLocation = maze[maze.length - 1][maze[0].length - 1];
        CELL_SIZE = 1/ (float) mazeSize;
        rectCircle = new Rectangle(MAZE_LEFT, MAZE_TOP, CELL_SIZE, CELL_SIZE);
        float bottom = Math.abs(MAZE_TOP) - CELL_SIZE;
        float right = Math.abs(MAZE_LEFT) - CELL_SIZE;
        rectCircleEnd = new Rectangle(right, bottom, CELL_SIZE, CELL_SIZE);
    }
    private void registerKeys(){
        // Register the inputs we want to have invoked
        inputKeyboard.registerCommand(GLFW_KEY_W, true, (double elapsedTime) -> {
            moveUp(CELL_SIZE);
        });
        inputKeyboard.registerCommand(GLFW_KEY_S, true, (double elapsedTime) -> {
            moveDown(CELL_SIZE);
        });
        inputKeyboard.registerCommand(GLFW_KEY_A, true, (double elapsedTime) -> {
            moveLeft(CELL_SIZE);
        });
        inputKeyboard.registerCommand(GLFW_KEY_D, true, (double elapsedTime) -> {
            moveRight(CELL_SIZE);
        });

        inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> {
            moveUp(CELL_SIZE);
        });
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> {
            moveDown(CELL_SIZE);
        });
        inputKeyboard.registerCommand(GLFW_KEY_LEFT, true, (double elapsedTime) -> {
            moveLeft(CELL_SIZE);
        });
        inputKeyboard.registerCommand(GLFW_KEY_RIGHT, true, (double elapsedTime) -> {
            moveRight(CELL_SIZE);
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
            gameState = GameState.STARTGAME;
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
            mazeSize = 20;
            startGame();
        });

        inputKeyboard.registerCommand(GLFW_KEY_F5, true, (double elapsedTime) -> {
            gameState = GameState.HIGHSCORES;
        });

        inputKeyboard.registerCommand(GLFW_KEY_F6, true, (double elapsedTime) -> {
            gameState = GameState.CREDITS;
        });
    }

    private void setupMaze(){
        Set<List<Integer>> notInMaze = new HashSet<>();
        Set<List<Integer>> frontier = new HashSet<>();

        for (int x = 0; x < mazeSize; x++) {
            for (int y = 0; y < mazeSize; y++) {
                maze[x][y] = new MazeCell(x,y);
                notInMaze.add(List.of(x, y));
            }
        }

        characterLocation = maze[0][0];
        characterLocation.setVisited(true);
        List<Integer> startCell = List.of(0,0);
        notInMaze.remove(startCell);
        //Add cell to maze, add its neighbors to the frontier
        notInMaze.remove(startCell);

        HashMap<String, List<Integer>> neighbors = MazeUtils.getNeighbors(startCell, mazeSize);
        for (Map.Entry<String, List<Integer>> entry: neighbors.entrySet()) {
            frontier.add(entry.getValue());
        }

        while (!notInMaze.isEmpty()) {
            //Now select a cell from the frontier and add it to the maze
            List<Integer> frontierCell = MazeUtils.getRandomFromHashSet(frontier);
            notInMaze.remove(frontierCell);
            frontier.remove(frontierCell);

            //Get neighbors of selected frontier cell
            HashMap<String, List<Integer>> frontierNeighbors = MazeUtils.getNeighbors(frontierCell, mazeSize);

            //Get the neighbors of the frontier cell that are in the maze, add neighbors not in maze to frontier
            HashMap<String, List<Integer>> frontierNeighborsInMaze = new HashMap<>();
            for (Map.Entry<String, List<Integer>> entry: frontierNeighbors.entrySet()) {
                if (!notInMaze.contains(entry.getValue())) {
                    frontierNeighborsInMaze.put(entry.getKey(), entry.getValue());
                }
                else{
                    frontier.add(entry.getValue());
                }
            }

            //Randomly select one of the frontier neighbors in the maze to connect to
            Map.Entry<String, List<Integer>> randomNeighbor = MazeUtils.getRandomFromHashMap(frontierNeighborsInMaze);

            //Now connect the frontier cell with that random neighbor
            List<Integer> randomNeighborIdx = randomNeighbor.getValue();

            switch (randomNeighbor.getKey()) {
                case "Top" -> {
                    maze[frontierCell.getFirst()][frontierCell.getLast()]
                            .setTop(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setBottom(maze[frontierCell.getFirst()][frontierCell.getLast()]);
                }
                case "Bottom" -> {
                    maze[frontierCell.getFirst()][frontierCell.getLast()]
                            .setBottom(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setTop(maze[frontierCell.getFirst()][frontierCell.getLast()]);
                }
                case "Left" -> {
                    maze[frontierCell.getFirst()][frontierCell.getLast()]
                            .setLeft(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setRight(maze[frontierCell.getFirst()][frontierCell.getLast()]);
                }
                case "Right" -> {
                    maze[frontierCell.getFirst()][frontierCell.getLast()]
                            .setRight(maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]);
                    maze[randomNeighborIdx.getFirst()][randomNeighborIdx.getLast()]
                            .setLeft(maze[frontierCell.getFirst()][frontierCell.getLast()]);
                }
            }
            notInMaze.remove(randomNeighborIdx);
            frontier.remove(randomNeighborIdx);
        }
        MazeUtils.cliRender(maze);
    }

    private void moveUp(float distance) {
        previousLocation = characterLocation;
        if ((characterLocation.getRow() - 1) >= 0 &&
        characterLocation.getTop() != null){
            rectCircle.top = rectCircle.top - distance;
            characterLocation = maze[characterLocation.getRow()-1][characterLocation.getColumn()];
        }
    }

    private void moveDown(float distance) {
        previousLocation = characterLocation;
        if ((characterLocation.getRow() + 1) < mazeSize &&
        characterLocation.getBottom() != null){
            rectCircle.top = rectCircle.top + distance;
            characterLocation = maze[characterLocation.getRow()+1][characterLocation.getColumn()];
        }
    }

    private void moveLeft(float distance) {
        previousLocation = characterLocation;
        if ((characterLocation.getColumn() - 1) >= 0 &&
        characterLocation.getLeft() != null){
            rectCircle.left = rectCircle.left - distance;
            characterLocation = maze[characterLocation.getRow()][characterLocation.getColumn()-1];
        }
    }

    private void moveRight(float distance) {
        previousLocation = characterLocation;
        if ((characterLocation.getColumn() + 1) < mazeSize &&
                characterLocation.getRight() != null){
            rectCircle.left = rectCircle.left + distance;
            characterLocation = maze[characterLocation.getRow()][characterLocation.getColumn()+1];
        }
    }


    public void shutdown() {
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
            if (previousLocation != null) {
                previousLocation.setScoreComputed(false);
            }
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
                scoreList.add("Score: " + Float.toString(score) + ", Maze Size: " + Integer.toString(mazeSize) + "\n");
                gameState = GameState.ENDGAME;
            }
        }
    }

    private void drawTextWithNewLines(String text, float top, float left, float height) {
        String[] stringArr = text.split("\n");

        int idx = 0;
        for (String str: stringArr){
            float newTop = top + (idx * height);
            graphics.drawTextByHeight(font, str, left, newTop, height, Color.BLACK);
            idx++;
        }
    }

    private void render(long window, double elapsedTime) {
        graphics.begin();

        switch (gameState){
            case STARTGAME -> {
                StringBuilder menuBuilder = new StringBuilder();
                graphics.draw(displayRect, Color.WHITE);
                menuBuilder.append("Welcome to the maze game\n");
                menuBuilder.append(instructionText);
                drawTextWithNewLines(menuBuilder.toString(), MENU_TOP, MENU_LEFT, TEXT_HEIGHT);
            }
            case ENDGAME -> {
                StringBuilder menuBuilder = new StringBuilder();
                menuBuilder.append("Game Over, Score:").append(score).append("\n");
                menuBuilder.append(instructionText);
                drawTextWithNewLines(menuBuilder.toString(), MENU_TOP, MENU_LEFT, TEXT_HEIGHT);
                score = 0;
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

                String timeAndScoreText = "Time " + timePassed + "\n" +
                        "Score " + score;

                drawTextWithNewLines(instructionText, -0.5f, -0.95f, TEXT_HEIGHT);
                drawTextWithNewLines(timeAndScoreText, -0.5f, 0.55f, TEXT_HEIGHT);
                drawTextWithNewLines(scoreListBuilder.toString(), -0.1f, -0.95f, TEXT_HEIGHT);

                graphics.draw(circle, rectCircle, 0, new Vector2f(rectCircle.left + rectCircle.width / 2, rectCircle.top + rectCircle.height / 2), Color.WHITE);
                graphics.draw(endCircle, rectCircleEnd, 0, new Vector2f(rectCircle.left + rectCircle.width / 2, rectCircle.top + rectCircle.height / 2), Color.WHITE);
            }
            case CREDITS -> {
                graphics.draw(displayRect, Color.WHITE);
                graphics.drawTextByHeight(font, "Made by Isaac Peterson", MENU_LEFT, MENU_TOP, TEXT_HEIGHT, Color.BLACK);
            }
            case HIGHSCORES -> {
                graphics.draw(displayRect, Color.WHITE);

                StringBuilder scoreListBuilder = new StringBuilder();
                scoreListBuilder.append("High Scores: \n");

                for (String score: scoreList){
                    scoreListBuilder.append(score);
                }
                drawTextWithNewLines(scoreListBuilder.toString(), -0.1f, -0.1f, TEXT_HEIGHT);
            }
        }
        graphics.end();
    }

    private void renderCell(MazeCell cell){
        if (cell.getTop() == null){
            float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS);

            graphics.draw(r, Color.YELLOW);

        }
        if (cell.getBottom() == null){
            float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE;
            float top = MAZE_TOP + (cell.getRow() + 1) * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS);

            graphics.draw(r, Color.YELLOW);

        }
        if (cell.getLeft() == null){
            float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE);

            graphics.draw(r, Color.YELLOW);
        }
        if (cell.getRight() == null){
            float left = MAZE_LEFT + (cell.getColumn() + 1) * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE);

            graphics.draw(r, Color.YELLOW);
        }

        float left = MAZE_LEFT + cell.getColumn() * CELL_SIZE + (1.0f/2.1f)*CELL_SIZE;
        float top = MAZE_LEFT + cell.getRow() * CELL_SIZE + (1.0f/2.1f)*CELL_SIZE;
        Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_WALL_THICKNESS);
        graphics.draw(r, Color.CORNFLOWER_BLUE);

        float dotSize = CELL_WALL_THICKNESS*3;

        if (cell.isVisited() & showBreadCrumbs){
            left = MAZE_LEFT + cell.getColumn() * CELL_SIZE + (1.0f/2.1f)*CELL_SIZE;
            top = MAZE_LEFT + cell.getRow() * CELL_SIZE + (1.0f/2.1f)*CELL_SIZE;
            r = new Rectangle(left, top, dotSize, dotSize);

            graphics.draw(r, Color.YELLOW);
        }

        if (cell.isOnShortestPath() & showPath){
            left = MAZE_LEFT + cell.getColumn() * CELL_SIZE + (1.0f/2.1f)*CELL_SIZE;
            top = MAZE_LEFT + cell.getRow() * CELL_SIZE + (1.0f/2.1f)*CELL_SIZE;
            r = new Rectangle(left, top, dotSize, dotSize);

            graphics.draw(r, Color.BLUE);
        }

        if (cell.isOnShortestPath() & showHint){
            if (MazeUtils.areNeighbors(characterLocation, cell)){
                left = MAZE_LEFT + cell.getColumn() * CELL_SIZE + (1.0f/2.1f)*CELL_SIZE;
                top = MAZE_LEFT + cell.getRow() * CELL_SIZE + (1.0f/2.1f)*CELL_SIZE;
                r = new Rectangle(left, top, dotSize, dotSize);

                graphics.draw(r, Color.BLUE);
            }
        }
    }
}
