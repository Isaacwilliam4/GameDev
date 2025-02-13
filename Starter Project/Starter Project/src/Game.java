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
    private final int mazeSize = 3;
    private final float fMazeSize = (float) mazeSize;
    private final float CELL_SIZE = 1 / fMazeSize;
    private final float CELL_WALL_THICKNESS = 0.002f;
    private final float MAZE_LEFT = -0.5f;
    private final float MAZE_TOP = -0.5f;
    private final float MAZE_BOTTOM = 0.5f - CELL_SIZE;
    private final float MAZE_RIGHT = 0.5f - CELL_SIZE;
    private final String instructionText;
    private MazeCell[][] maze;
    private final Rectangle rectCircle = new Rectangle(MAZE_LEFT, MAZE_TOP, CELL_SIZE, CELL_SIZE);
    private final Rectangle rectCircleEnd = new Rectangle(MAZE_BOTTOM, MAZE_RIGHT, CELL_SIZE, CELL_SIZE);
    private Texture circle;
    private Texture endCircle;
    private Font font;
    private MazeCell characterLocation;
    private final KeyboardInput inputKeyboard;
    private final List<Integer> startLocation = List.of(0, 0);
    private final List<Integer> endLocation = List.of(mazeSize-1,mazeSize-1);
    private boolean showHint = false;
    private boolean showBreadCrumbs = false;
    private boolean showPath = false;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.inputKeyboard = new KeyboardInput(graphics.getWindow());
        this.instructionText = "5x5 Maze - f1\n" +
                "10x10 Maze - f2\n" +
                "15x15 Maze - f3\n" +
                "20x20 Maze - f4\n" +
                "Display High Scores - f5\n" +
                "Display Credits - f6\n";
    }

    public void initialize() {
        endCircle = new Texture("resources/images/greencircle.png");
        circle = new Texture("resources/images/bluecircle.png");
        font = new Font("resources/fonts/Blacknorthdemo-mLE25.otf", 16, false);

        setupMaze();
        registerKeys();
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
    }

    private void setupMaze(){
        maze = new MazeCell[mazeSize][mazeSize];
        Set<List<Integer>> notInMaze = new HashSet<>();
        Set<List<Integer>> frontier = new HashSet<>();

        for (int x = 0; x < mazeSize; x++) {
            for (int y = 0; y < mazeSize; y++) {
                maze[x][y] = new MazeCell(x,y);
                notInMaze.add(List.of(x, y));
            }
        }

        characterLocation = maze[startLocation.getFirst()][startLocation.getLast()];
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
        if ((characterLocation.getRow() - 1) >= 0 &&
        characterLocation.getTop() != null){
            rectCircle.top = rectCircle.top - distance;
            characterLocation = maze[characterLocation.getRow()-1][characterLocation.getColumn()];
        }
    }

    private void moveDown(float distance) {
        if ((characterLocation.getRow() + 1) < mazeSize &&
        characterLocation.getBottom() != null){
            rectCircle.top = rectCircle.top + distance;
            characterLocation = maze[characterLocation.getRow()+1][characterLocation.getColumn()];
        }
    }

    private void moveLeft(float distance) {
        if ((characterLocation.getColumn() - 1) >= 0 &&
        characterLocation.getLeft() != null){
            rectCircle.left = rectCircle.left - distance;
            characterLocation = maze[characterLocation.getRow()][characterLocation.getColumn()-1];
        }
    }

    private void moveRight(float distance) {
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
        characterLocation.setVisited(true);
        MazeUtils.updateShortestPath(maze, characterLocation, endLocation);
    }

    private void render(long window, double elapsedTime) {
        graphics.begin();

        for (var row: maze){
            for (var cell:row){
                renderCell(cell);
            }
        }

        String[] stringArr = instructionText.split("\n");

        float width = 0.40f; // we are deciding the width
        float left = -0.95f; // center horizontally
        float top = -0.1f; // center vertically
        float height = 1f;
        for (String str: stringArr){
            float newHeight = font.measureTextHeight(str, width);
            if (newHeight < height){
                height = newHeight;
            }
        }

        int idx = 0;
        for (String str: stringArr){
            float newTop = top - (idx * height);
            graphics.drawTextByHeight(font, str, left, newTop, height, Color.BLACK);
            idx++;
        }

        graphics.draw(circle, rectCircle, 0, new Vector2f(rectCircle.left + rectCircle.width / 2, rectCircle.top + rectCircle.height / 2), Color.WHITE);
        graphics.draw(endCircle, rectCircleEnd, 0, new Vector2f(rectCircle.left + rectCircle.width / 2, rectCircle.top + rectCircle.height / 2), Color.WHITE);
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
