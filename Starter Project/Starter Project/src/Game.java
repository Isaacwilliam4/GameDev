import com.sun.tools.javac.Main;
import edu.usu.graphics.*;
import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;

import java.awt.*;
import java.util.*;
import java.util.List;
import edu.usu.graphics.*;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private final int mazeSize = 20;
    private final float fMazeSize = (float) mazeSize;
    private final float MAZE_LEFT = -0.5f;
    private final float MAZE_TOP = -0.5f;
    private final float CELL_SIZE = 1 / fMazeSize;
    private final float CELL_WALL_THICKNESS = 0.005f;
    private MazeCell[][] maze;
    private final Rectangle rectCircle = new Rectangle(MAZE_LEFT, MAZE_TOP, CELL_SIZE, CELL_SIZE);
    private Texture circle;
    private MazeCell characterLocation = new MazeCell(0,0);
    private final KeyboardInput inputKeyboard;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.inputKeyboard = new KeyboardInput(graphics.getWindow());
    }

    public void initialize() {
        setupMaze();
        registerKeys();
    }

    private void registerKeys(){
        circle = new Texture("resources/images/circle.jpg");

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

        List<Integer> startCell = List.of(0,0);
        notInMaze.remove(startCell);
        //Add cell to maze, add its neighbors to the frontier
        notInMaze.remove(startCell);

        HashMap<String, List<Integer>> neighbors = getNeighbors(startCell);
        for (Map.Entry<String, List<Integer>> entry: neighbors.entrySet()) {
            frontier.add(entry.getValue());
        }

        while (!notInMaze.isEmpty()) {
            //Now select a cell from the frontier and add it to the maze
            List<Integer> frontierCell = getRandomFromHashSet(frontier);
            notInMaze.remove(frontierCell);
            frontier.remove(frontierCell);

            //Get neighbors of selected frontier cell
            HashMap<String, List<Integer>> frontierNeighbors = getNeighbors(frontierCell);

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
            Map.Entry<String, List<Integer>> randomNeighbor = getRandomFromHashMap(frontierNeighborsInMaze);

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
    }

    private static Map.Entry<String, List<Integer>> getRandomFromHashMap(HashMap<String, List<Integer>> set){
        Random rand = new Random();
        int randInt = rand.nextInt(set.size());
        int idx = 0;
        Map.Entry<String, List<Integer>> randCell = null;

        for (Map.Entry<String, List<Integer>> entry: set.entrySet()) {
            if (randInt == idx){
                randCell = entry;
                break;
            }
            idx++;
        }

        return randCell;
    }

    private static List<Integer> getRandomFromHashSet(Set<List<Integer>> set){
        Random rand = new Random();
        int randInt = rand.nextInt(set.size());
        int idx = 0;
        List<Integer> randCell = null;

        for (List<Integer> value: set) {
            if (randInt == idx){
                randCell = value;
                break;
            }
            idx++;
        }

        return randCell;
    }

    private void cliRender(){
        StringBuilder[][] cliMaze = new StringBuilder[mazeSize][mazeSize];
        for (int x = 0; x < cliMaze.length; x++) {
            for (int y = 0; y < cliMaze[x].length; y++) {
                cliMaze[x][y] = new StringBuilder("    ");
            }
        }

        for (var row: maze){
            for (var cell:row){
                cliRenderCell(cell, cliMaze);
            }
        }

        StringBuilder cliMazeText = new StringBuilder();
        for (int x = 0; x < cliMaze.length; x++) {
            for (int y = 0; y < cliMaze[0].length; y++) {
                cliMazeText.append(cliMaze[x][y].toString());
            }
            cliMazeText.append("\n");
        }

        System.out.println(cliMazeText);
    }

    private static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(from, index);
        }
    }

    private void cliRenderCell(MazeCell cell, StringBuilder[][] cliMaze) {
        if (cell.getTop() == null){
            if ((cell.getRow() - 1) >= 0){
                StringBuilder builder = cliMaze[cell.getRow()-1][cell.getColumn()];
                builder.replace(1, 3,"__");
            }
        }
        if (cell.getBottom() == null){
            StringBuilder builder = cliMaze[cell.getRow()][cell.getColumn()];
            builder.replace(1, 3,"__");
        }
        if (cell.getLeft() == null){
            cliMaze[cell.getRow()][cell.getColumn()].replace(0, 1,"|");
        }
        if (cell.getRight() == null){
            cliMaze[cell.getRow()][cell.getColumn()].replace(3, 4,"|");
        }
    }

    private HashMap<String, List<Integer>> getNeighbors(List<Integer> block) {
        HashMap<String, List<Integer>> neighbors = new HashMap<>();
        if ((block.getLast() - 1) >= 0){
            neighbors.put("Left", List.of(block.getFirst(), block.getLast() - 1));
        }
        if ((block.getLast() + 1) < mazeSize){
            neighbors.put("Right", List.of(block.getFirst(), block.getLast() + 1));
        }
        if ((block.getFirst() - 1) >= 0){
            neighbors.put("Top", List.of(block.getFirst() - 1, block.getLast()));
        }
        if ((block.getFirst() + 1) < mazeSize){
            neighbors.put("Bottom", List.of(block.getFirst() + 1, block.getLast()));
        }
        return neighbors;
    }

    private void moveUp(float distance) {
        if ((characterLocation.getRow() - 1) >= 0){
            rectCircle.top = rectCircle.top - distance;
            characterLocation.setRow(characterLocation.getRow() - 1);
        }
    }

    private void moveDown(float distance) {
        if ((characterLocation.getRow() + 1) < mazeSize){
            rectCircle.top = rectCircle.top + distance;
            characterLocation.setRow(characterLocation.getRow() + 1);
        }
    }

    private void moveLeft(float distance) {
        if ((characterLocation.getColumn() - 1) >= 0){
            rectCircle.left = rectCircle.left - distance;
            characterLocation.setColumn(characterLocation.getColumn() - 1);
        }

    }

    private void moveRight(float distance) {
        if ((characterLocation.getColumn() + 1) < mazeSize){
            rectCircle.left = rectCircle.left + distance;
            characterLocation.setColumn(characterLocation.getColumn() + 1);
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

//        // If user presses ESC, then exit the program
//        if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
//            glfwSetWindowShouldClose(graphics.getWindow(), true);
//        }
        inputKeyboard.update(elapsedTime);

        //Move Character
//        characterLocation = characterLocation.getBottom();
    }

    private void update(double elapsedTime) {
    }

    private void render(long window, double elapsedTime) {
        graphics.begin();

        for (var row: maze){
            for (var cell:row){
                renderCell(cell);
            }
        }
        graphics.draw(circle, rectCircle, 0, new Vector2f(rectCircle.left + rectCircle.width / 2, rectCircle.top + rectCircle.height / 2), Color.WHITE);

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
    }
}
