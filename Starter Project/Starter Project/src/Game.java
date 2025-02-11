import com.sun.tools.javac.Main;
import edu.usu.graphics.*;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private MazeCell[][] maze;
    private final int mazeSize = 3;
    private MazeCell characterLocation;
    private Set<List<Integer>> frontier;
    private Set<List<Integer>> notInMaze;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {
        maze = new MazeCell[mazeSize][mazeSize];
        notInMaze = new HashSet<>();
        frontier = new HashSet<>();

        for (int x = 0; x < mazeSize; x++) {
            for (int y = 0; y < mazeSize; y++) {
                maze[x][y] = new MazeCell(x,y);
                notInMaze.add(List.of(x, y));
            }
        }

        List<Integer> currentBlockIdx = List.of(0,0);
        notInMaze.remove(currentBlockIdx);

        Random rand = new Random();

        while (!notInMaze.isEmpty()) {
            HashMap<String, List<Integer>> neighbors = getNeighbors(currentBlockIdx);
            HashMap<String, List<Integer>> frontier = new HashMap<>();
            for (Map.Entry<String, List<Integer>> entry: neighbors.entrySet()) {
                if (notInMaze.contains(entry.getValue())) {
                    frontier.put(entry.getKey(), entry.getValue());
                }
            }

            int randNum = rand.nextInt(frontier.size());
            int idx = 0;
            Map.Entry<String, List<Integer>> nextBlock = null;

            for (Map.Entry<String, List<Integer>> entry: frontier.entrySet()) {
                if (randNum == idx){
                    nextBlock = entry;
                }
            }

            List<Integer> nextBlockIdx = nextBlock.getValue();
            notInMaze.remove(nextBlockIdx);

            if (nextBlock.getKey().equals("Top")){
                maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]
                        .setTop(maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]);
                maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]
                        .setBottom(maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]);
            }
            else if (nextBlock.getKey().equals("Bottom")){
                maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]
                        .setBottom(maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]);
                maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]
                        .setTop(maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]);
            }
            else if (nextBlock.getKey().equals("Left")){
                maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]
                        .setLeft(maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]);
                maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]
                        .setRight(maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]);
            }
            else if (nextBlock.getKey().equals("Right")){
                maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]
                        .setRight(maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]);
                maze[nextBlockIdx.getFirst()][nextBlockIdx.getLast()]
                        .setLeft(maze[currentBlockIdx.getFirst()][currentBlockIdx.getLast()]);
            }









        }






    }

    private HashMap<String, List<Integer>> getNeighbors(List<Integer> block) {
        HashMap<String, List<Integer>> neighbors = new HashMap<>();
        if ((block.getFirst() - 1) >= 0){
            neighbors.put("Left", (List.of(block.getFirst() - 1, block.get(1))));
        }
        if ((block.getFirst() + 1) < mazeSize){
            neighbors.put("Right", (List.of(block.getFirst() + 1, block.get(1))));
        }
        if ((block.getLast() - 1) >= 0){
            neighbors.put("Bottom", List.of(block.getLast() - 1, block.get(1))));
        }
        if ((block.getLast() + 1) < mazeSize){
            neighbors.put("Top", List.of(block.getLast() + 1, block.get(1))));
        }
        return neighbors;
    }
    public void shutdown() {
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

        // If user presses ESC, then exit the program
        if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        }

        //Move Character
//        characterLocation = characterLocation.getBottom();
    }

    private void update(double elapsedTime) {
    }

    private void render(double elapsedTime) {
        graphics.begin();

        for (var row: maze){
            for (var cell:row){
                renderCell(cell);
            }
        }
        graphics.end();
    }

    private void renderCell(MazeCell cell){
        final float MAZE_LEFT = -0.5f;
        final float MAZE_TOP = -0.5f;
        final float CELL_SIZE = 1 / 3.0f;
        final float CELL_WALL_THICKNESS = CELL_SIZE * 0.01f;

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
